package com.nhb.dtu.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.nhb.dtu.dto.ElecParamAnalyseDTO;
import com.nhb.dtu.dto.ModbusGericProtocolDTO;
import com.nhb.dtu.dto.PacketRegisterRangeDTO;
import com.nhb.dtu.entity.ElecParamAnalyse;
import com.nhb.dtu.entity.ModbusGericProtocol;
import com.nhb.dtu.entity.PacketRegisterRange;
import com.nhb.dtu.enums.ElectricityType;
import com.nhb.dtu.protocol.Meter_ModelBus_Generic;
import com.nhb.dtu.service.basic.ElecParamAnalyseService;
import com.nhb.dtu.service.basic.ModbusGericProtocolService;
import com.nhb.dtu.service.basic.PacketRegisterRangeService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;

public class InitModbusGenericProMap {

	private static Logger logger = LoggerFactory.getLogger(InitModbusGenericProMap.class);

	private static ModbusGericProtocolService getModbusGericProtocolService() {
		return SpringContextHolder.getBean("modbusGericProtocolService");
	}

	private static PacketRegisterRangeService getPacketRegisterRangeService() {
		return SpringContextHolder.getBean("packetRegisterRangeService");
	}

	private static ElecParamAnalyseService getElecParamAnalyseService() {
		return SpringContextHolder.getBean("elecParamAnalyseService");
	}

	private static Map<String, Class<?>> modbusGenericProMap = new HashMap<>();

	public static Map<String, Class<?>> getModbusGenericProMap() {
		return modbusGenericProMap;
	}

	public static void setModbusGenericProMap(Map<String, Class<?>> modbusGenericProMap) {
		InitModbusGenericProMap.modbusGenericProMap = modbusGenericProMap;
	}

	public static void initModbusGenericMap() throws Exception {
		List<ModbusGericProtocol> protocols = getModbusGericProtocolService().findAll();
		List<PacketRegisterRange> ranges = getPacketRegisterRangeService().findAll();
		List<ElecParamAnalyse> analyses = getElecParamAnalyseService().findAll();

		// 转换成DTO
		List<ModbusGericProtocolDTO> protocolDTOs = Lists.newArrayList();
		ModbusGericProtocolDTO mdto = null;
		for (ModbusGericProtocol protocol : protocols) {
			mdto = new ModbusGericProtocolDTO();
			mdto.setId(protocol.getId());
			mdto.setProtocol(protocol.getProtocol());
			for (ElectricityType type : ElectricityType.values()) {
				if (protocol.getElectricityType().equals(type.toString())) {
					mdto.setElectricityType(type);
				}
			}
			protocolDTOs.add(mdto);
		}

		List<PacketRegisterRangeDTO> rangeDTOs = Lists.newArrayList();
		PacketRegisterRangeDTO pdto = null;
		for (PacketRegisterRange range : ranges) {
			pdto = new PacketRegisterRangeDTO();
			for (ModbusGericProtocolDTO protocol : protocolDTOs) {
				if (range.getProtocolId().equals(protocol.getId())) {
					pdto.setId(range.getId());
					pdto.setEndAt(range.getEndAt());
					pdto.setStartAt(range.getStartAt());
					pdto.setModbusGericProtocol(protocol);
					rangeDTOs.add(pdto);
				}
			}
		}

		List<ElecParamAnalyseDTO> analyseDTOs = Lists.newArrayList();
		ElecParamAnalyseDTO eDto = null;
		for (ElecParamAnalyse analyse : analyses) {
			eDto = new ElecParamAnalyseDTO();
			for (PacketRegisterRangeDTO range : rangeDTOs) {
				if (analyse.getPacketId().equals(range.getId())) {
					eDto.setCalculateStr(analyse.getCalculateStr());
					eDto.setElecParam(analyse.getElecParam());
					eDto.setId(analyse.getId());
					eDto.setLoopNo(analyse.getLoopNo());
					eDto.setRegisterCount(analyse.getRegisterCount());
					eDto.setStartAt(analyse.getStartAt());
					eDto.setPacketRegisterRange(range);
					analyseDTOs.add(eDto);
				}
			}
		}

		// 记录每个协议类的发包个数,key为通用协议的标识，value为发包的个数
		Map<String, Integer> proPacketCountMap = doGetProPacketCountMap(protocolDTOs, rangeDTOs);
		int index = 0;
		for (ModbusGericProtocolDTO modbusGericProtocol : protocolDTOs) {
			logger.info(modbusGericProtocol.getProtocol());
			ClassPool pool = ClassPool.getDefault();
			// 引入包
			pool.importPackage("java.util.Date");
			pool.importPackage("java.util.List");
			pool.importPackage("com.nhb.dtu.protocol.CRC");
			pool.importPackage("com.nhb.dtu.entity.DataElectricity");
			pool.importPackage("com.nhb.dtu.entity.DataElectricity3Phase");
			pool.importPackage("com.nhb.dtu.entity.ReceiptDevice");
			pool.importPackage("com.nhb.dtu.entity.ReceiptMeter");
			pool.importPackage("com.nhb.dtu.entity.ReceiptCollector");
			pool.importPackage("com.nhb.dtu.entity.ElectricityType");
			// 创建动态类
			/**
			 * If a program is running on a web application server such as JBoss
			 * and Tomcat, the ClassPool object may not be able to find user
			 * classes
			 */
			pool.insertClassPath(new ClassClassPath(Meter_ModelBus_Generic.class));
			CtClass cc = pool.get("com.nhb.dtu.protocol.Meter_ModelBus_Generic");
			cc.setName("Meter_ModelBus_Generic" + index);
			index++;
			// 给动态类添加属性
			for (ElecParamAnalyseDTO elecParamAnalyse : analyseDTOs) {
				if (elecParamAnalyse.getPacketRegisterRange().getModbusGericProtocol().getId() == modbusGericProtocol
						.getId()) {
					CtField cf = new CtField(doGetFieldType(pool, elecParamAnalyse.getElecParam()),
							elecParamAnalyse.getElecParam() + "_" + elecParamAnalyse.getLoopNo(), cc);
					cf.setModifiers(Modifier.PRIVATE);
					cc.addField(cf);
				}
			}
			// 给动态类添加组帧的方法
			String buildWritingFrames = makePacketFrame(rangeDTOs, modbusGericProtocol.getId());
			CtMethod m = cc.getDeclaredMethod("buildWritingFrames");
			m.insertBefore(buildWritingFrames);
			logger.info(buildWritingFrames);

			// 添加解帧和计算字段值的方法
			int count = proPacketCountMap.get(modbusGericProtocol.getProtocol());
			String analysFrameBefore = makeAnalysFrameBefore(count, rangeDTOs, analyseDTOs,
					modbusGericProtocol.getId());
			CtMethod analyseMethod = cc.getDeclaredMethod("analyzeFrame");
			analyseMethod.insertBefore(analysFrameBefore);
			logger.info(analysFrameBefore);
			// 添加入库方法
			String caculateLastResult = getLastResult(rangeDTOs, analyseDTOs, modbusGericProtocol.getId());
			CtMethod handleResult = cc.getDeclaredMethod("handleResult");
			String handleResultBefore = makehandleResultBefore(modbusGericProtocol, analyseDTOs);
			logger.info(caculateLastResult);
			logger.info(handleResultBefore);
			handleResult.insertBefore(caculateLastResult);
			handleResult.insertAfter(handleResultBefore);
			// 组成协议映射
			Class<?> clazz = cc.toClass();
			cc.freeze();
			cc.defrost();
			modbusGenericProMap.put(modbusGericProtocol.getProtocol(), clazz);
		}

	}

	/*
	 * 组织计算方法，将寄存器的初始值按照计算公式计算出最终值
	 */
	private static String getLastResult(List<PacketRegisterRangeDTO> packetRegisterRanges,
			List<ElecParamAnalyseDTO> elecParamAnalyses, Long protocolId) {
		String ret = "{";
		for (PacketRegisterRangeDTO packetRegisterRange : packetRegisterRanges) {
			if (packetRegisterRange.getModbusGericProtocol().getId() != protocolId) {
				continue;
			}
			for (ElecParamAnalyseDTO elecAnalyse : elecParamAnalyses) {
				if (elecAnalyse.getPacketRegisterRange().getId() != packetRegisterRange.getId()) {
					continue;
				}
				ret += elecAnalyse.getElecParam() + "_" + elecAnalyse.getLoopNo() + "=";
				String formula = elecAnalyse.getCalculateStr().replace("}", "_" + elecAnalyse.getLoopNo());
				formula = formula.replace("{", " ");
				ret += formula + ";";
			}
		}
		ret += "}";
		return ret;
	}

	/*
	 * 将计算出来的数据存储到数据表中
	 */
	private static String makehandleResultBefore(ModbusGericProtocolDTO modbusGericProtocol,
			List<ElecParamAnalyseDTO> elecParamAnalyses) {
		String ret = "{List receipCircuits = doGetReceiptCicuirt();" + "Date now = new Date();"
				+ "for(int i = 0; i < receipCircuits.size() ; i++ ){"
				+ "ReceiptDevice ReceiptDevice = (ReceiptDevice)receipCircuits.get(i);"
				+ "ReceiptMeter receiptMeter = getReceiptMeter();"
				+ "ReceiptCollector receiptCollector = getReceiptCollector();"
				+ "DataElectricity3Phase dataElectricity3Phase = null;"
				+ "DataElectricity dataElectricity = new DataElectricity();"
				+ "dataElectricity.setDeviceId(receiptCollector.getCollectorNo() + receiptMeter.getMeterNo() + ReceiptDevice.getId());"
				+ "dataElectricity.setReadTime(now);" + "dataElectricity.setElectricityType(\""
				+ String.valueOf(modbusGericProtocol.getElectricityType()) + "\");";
		for (ElecParamAnalyseDTO elecParamAnalyse : elecParamAnalyses) {
			if (elecParamAnalyse.getPacketRegisterRange().getModbusGericProtocol().getId() != modbusGericProtocol
					.getId() || elecParamAnalyse.getElecParam().startsWith("U")
					|| elecParamAnalyse.getElecParam().startsWith("I")) {
				continue;
			}
			if (isSplitPhaseVariable(elecParamAnalyse.getElecParam())) {
				ret += " if(dataElectricity3Phase == null){dataElectricity3Phase = new DataElectricity3Phase();}";
				ret += "if(ReceiptDevice.getCircuitNo().equals(\"" + elecParamAnalyse.getLoopNo() + "\")){";
				ret += " dataElectricity3Phase.set" + elecParamAnalyse.getElecParam().substring(0, 1).toUpperCase()
						+ elecParamAnalyse.getElecParam().substring(1) + "(new Double("
						+ elecParamAnalyse.getElecParam() + "_" + elecParamAnalyse.getLoopNo() + "));}";
				continue;
			}
			ret += "if(ReceiptDevice.getCircuitNo().equals(\"" + elecParamAnalyse.getLoopNo() + "\")){";
			ret += "dataElectricity.set" + elecParamAnalyse.getElecParam().substring(0, 1).toUpperCase()
					+ elecParamAnalyse.getElecParam().substring(1) + "(new Double(" + elecParamAnalyse.getElecParam()
					+ "_" + elecParamAnalyse.getLoopNo() + "));}";
		}
		ret += "saveDataToDataBase(dataElectricity,dataElectricity3Phase);";
		ret += "}}";
		return ret;
	}

	private static boolean isSplitPhaseVariable(String elecParam) {
		if (elecParam.equals("kwhA") || elecParam.equals("kwhB") || elecParam.equals("kwhC")
				|| elecParam.equals("kwhForwardA") || elecParam.equals("kwhForwardB") || elecParam.equals("kwhForwardC")
				|| elecParam.equals("kwhReverseA") || elecParam.equals("kwhReverseB") || elecParam.equals("kwhReverseC")
				|| elecParam.equals("kvarh1A") || elecParam.equals("kvarh1B") || elecParam.equals("kvarh1C")
				|| elecParam.equals("kvarh2A") || elecParam.equals("kvarh2B") || elecParam.equals("kvarh2C")) {
			return true;
		}
		return false;
	}

	/*
	 * 组织协议类的下发帧
	 */
	private static String makePacketFrame(List<PacketRegisterRangeDTO> prrs, Long protocolId) {
		String ret = "{int[] data = new int[8];byte[] frame;data[0] = Integer.parseInt(receiptMeter.getMeterNo());data[1] = 0x03;";
		for (PacketRegisterRangeDTO packetRegisterRange : prrs) {
			if (packetRegisterRange.getModbusGericProtocol().getId() == protocolId) {
				int startAt = Integer.parseInt(packetRegisterRange.getStartAt(), 16);
				int endAt = Integer.parseInt(packetRegisterRange.getEndAt(), 16);
				int length = endAt - startAt + 1;
				ret += " data[2] = 0x" + packetRegisterRange.getStartAt().substring(0, 2) + "; ";
				ret += " data[3] = 0x" + packetRegisterRange.getStartAt().substring(2, 4) + "; ";
				ret += " data[4] = " + (length / 256) + "; ";
				ret += " data[5] = " + (length % 256) + "; ";
				ret += " int[] crc = CRC.calculateCRC(data, 6); ";
				ret += " data[6] = crc[0]; ";
				ret += " data[7] = crc[1]; ";
				ret += " frame = new byte[data.length]; ";
				ret += " for (int i = 0; i < data.length; i++) { ";
				ret += " 	frame[i] = (byte) data[i]; ";
				ret += " } ";
				ret += " writingFrames.add(frame); ";
			}
		}
		ret += "}";
		return ret;
	}

	/*
	 * 组织解帧方法，解出寄存器的初始值
	 */
	private static String makeAnalysFrameBefore(int count, List<PacketRegisterRangeDTO> packetRegisterRanges,
			List<ElecParamAnalyseDTO> elecParamAnalyses, Long protocolId) {
		String ret = "{if (readingFrames.size() != " + count + ") {" + " return false; " + "} "
				+ " int[] data = new int[frame.length - 9];" + " for (int i = 0; i < data.length; i++) {"
				+ " data[i] = frame[i + 8] & 0xFF;" + "} "
				+ " int meterNo = Integer.parseInt(receiptMeter.getMeterNo()); " + " if(meterNo != data[0]){ "
				+ " return false; " + " } " + " if (!CRC.isValid(data)) " + " return false;";
		for (PacketRegisterRangeDTO packetRegisterRange : packetRegisterRanges) {
			if (packetRegisterRange.getModbusGericProtocol().getId() != protocolId) {
				continue;
			}
			int startAt = Integer.parseInt(packetRegisterRange.getStartAt(), 16);
			int endAt = Integer.parseInt(packetRegisterRange.getEndAt(), 16);
			int length = endAt - startAt + 1;
			ret += "if (data[2] == " + length * 2 + ") { ";
			for (ElecParamAnalyseDTO elecAnalyse : elecParamAnalyses) {
				if (elecAnalyse.getPacketRegisterRange().getId() != packetRegisterRange.getId()
						|| elecAnalyse.getStartAt().equals("")) {
					continue;
				}
				int elecParamStart = Integer.parseInt(elecAnalyse.getStartAt(), 16);
				ret += "" + elecAnalyse.getElecParam() + "_" + elecAnalyse.getLoopNo() + "=";
				if (elecAnalyse.getRegisterCount() == 1) {
					ret += "data[" + ((elecParamStart - startAt) * 2 + 3) + "] * 256 + data["
							+ ((elecParamStart - startAt) * 2 + 4) + "] ; ";
				} else {
					ret += "data[" + ((elecParamStart - startAt) * 2 + 3) + "] * 256 * 256 * 256 + data["
							+ ((elecParamStart - startAt) * 2 + 4) + "] * 256 * 256 + data["
							+ ((elecParamStart - startAt) * 2 + 5) + "] * 256 + data["
							+ ((elecParamStart - startAt) * 2 + 6) + "] ; ";
				}
			}
			ret += "}";
		}
		ret += " return true;}";
		return ret;
	}

	/*
	 * 获取电参量类型
	 */
	public static CtClass doGetFieldType(ClassPool pool, String elecParam) throws Exception {
		if (elecParam.equals("readTime")) {
			return pool.getCtClass("java.lang.String");
		}
		return CtClass.doubleType;
	}

	/*
	 * 记录每个协议类的发包个数,key为通用协议的标识，value为发包的个数
	 */
	private static Map<String, Integer> doGetProPacketCountMap(List<ModbusGericProtocolDTO> mgps,
			List<PacketRegisterRangeDTO> prrs) {
		Map<String, Integer> proPacketCountMap = new HashMap<>();
		for (ModbusGericProtocolDTO modbusGericProtocol : mgps) {
			Integer count = 0;
			for (PacketRegisterRangeDTO packetRegisterRange : prrs) {
				if (packetRegisterRange.getModbusGericProtocol().getId() == modbusGericProtocol.getId()) {
					count++;
				}
			}
			proPacketCountMap.put(modbusGericProtocol.getProtocol(), count);
		}
		return proPacketCountMap;
	}
}
