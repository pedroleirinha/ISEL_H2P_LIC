library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyTransmitter IS
	PORT(
		CLK ,TxClk, Load, CLEAR:	 	IN std_logic;
		D:		 						IN std_logic_vector(3 downto 0);
		TxD, KbFree:				OUT std_logic
	);
END KeyTransmitter;

ARCHITECTURE Behaviour OF KeyTransmitter IS
	
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	component ShiftRegisterL4
		PORT(
			CLK, CE, PL, CLEAR: 	IN std_logic;
			D: 						IN std_logic_vector(3 downto 0);
			Q:							OUT std_logic
		);
	end component;
	
	component KeyTransmitterControl
		PORT(
			clk_in, Load, CLEAR, CE: 					IN std_logic;
			regEn, kbFree, startSignal, endSignal: OUT std_logic
		);
	end component;
	
	signal DAV, CTS, DAC, Wreg, regEn, startSignal, endSignal, sendBit, shiftBit: std_logic;
	signal bufferK: std_logic_vector(3 downto 0);
	
BEGIN
	
	reg: RegistryL4 port map(
		clk_in 	=> Load,
		CLEAR  	=> CLEAR,
		D 			=> D,
		CE 		=> regEn,
		Q 			=> bufferK
	);

	shiftRegister1: ShiftRegisterL4 port map(
		CLK 		=> TxClk,
		CE 		=> '1',
		PL 		=> Load,		
		CLEAR		=> CLEAR,
		D			=>	bufferK,
		Q 			=> shiftBit
	);

	
	control: KeyTransmitterControl port map(
		clk_in 		=> CLK,
		CE 			=> '1',
		Load 			=> Load,		
		CLEAR			=> CLEAR,
		regEn			=>	regEn,
		kbFree 		=> KbFree,
		startSignal => startSignal,
		endSignal	=> endSignal
	);
	
	TxD	<=	'1' when startSignal = '1' else
				'0' when endSignal = '1' else
				shiftBit;
	
	

END Behaviour;