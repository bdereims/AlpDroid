package com.alpdroid.huGen10
/**
 *   ECU Address data for CAN ECU
**/
// Dans certains cas rares, les 2 BUS utilisent la même adresse, il faut distringuer les BUS
// A priori concerne uniquement :
/* <SentBytes>03B7</SentBytes>
   <SentBytes>0405</SentBytes>
   <SentBytes>0589</SentBytes>
   <SentBytes>0597</SentBytes>
*/
enum class CanECUAddrs(val idcan: Int) {
	EMM_CANHS_R_01(0x0211),
	CLIM_CANHS_R_03(0x0392),
	AT_CANHS_R_01(0x04AD),
	MMI_TPMS_CANHS_R_01(0x0673),
	MMI_BRAKE_CANHS_Rst_01(0x0597), //doublon
	GCS_CANHS_Rst_01(0x0589), // doublon
	CLUSTER_CANHS_R_01(0x06FB),
	CLUSTER_CANHS_R_04(0x03B7), // doublon
	ECM_CANHS_RNr_01(0x01F6),
	AT_CANV_Rst_01(0x017C),
	BCM_CANHS_R_04(0x0350),
	BCM_CANHS_R_05(0X055D),
	BCM_CANHS_R_08(0x04AC),
	BCM_CANHS_R_09(0x01B0),
	BCM_to_ESCL3(0x0505),
	BRAKE_CANHS_RNr_01(0x0242),
	BRAKE_CANHS_RNr_02(0x029A),
	BRAKE_CANHS_RNr_03(0x029C),
	CLUSTER_CANHS_R_05(0x0646),
	MMI_BRAKE_CANHS_RNr_01(0x0666),
	TPMS_Rst1(0x043B),
	TORQUE_ECM_CANHS_RNr_01(0x0186),
	TORQUE_AT_CANHS_RNr_01(0x017A),
	ECM_CANHS_R_03(0x0217),
	SSCU_CANHS_R_02(0x0218),
	CANMCUSEND(0x712), // Audio - Audio Unit
	CANMCUREC(0x732),
	CANECUBASE(0X7DF), // Broadcast
	CANECUSEND_0(0x7E0),
	CANECUSEND_SCR(0x7E6),
	CANECUREC_0(0x7E8),
	CANECUSEND_ECM(0x7E1),
	CANECUREC_ECM(0x7E9), // Engine Control Module
	CANECUREC_SCR(0x7EE), // Selective Catalytic Reduction
	CANECUSEND_ABS(0x740), // ABS ESP Automatic Brake Parking
	CANECUSEND_EPS(0x742), // Direction assistée
	CANECUSEND_ETT(0x743), // ECU Entretien
	CANECUSEND_CLIM(0x744), // Clim
	CANECUSEND_TPS(0x745), // TPMS
	CANMCUSEND_MMU(0x747), // Navigation / Multimedia / Télémétrie
	CANECUSEND_EMM(0x74D), // Engine Management Module
	CANECUSEND_AUP(0x74E), // Parking Sonar
	CANECUSEND_SRS(0x752), // Airbag
	CANECUSEND_APB(0x755), // Brake Parking
	CANECUREC_ABS(0x760),
	CANECUREC_EPS(0x762),
	CANECUREC_ETT(0x763),
	CANECUREC_CLIM(0x764),
	CANECUREC_TPS(0x765),
	CANMCUREC_MMU(0x767),
	CANECUREC_EMM(0x76D),
	CANECUREC_AUP(0x76E),
	CENECUREC_SRS(0x772), // srs esb
	CANECUREC_APB(0x775),  // frein de parking

}
