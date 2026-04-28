library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyTransmitter IS
	PORT(
		CLK ,TxClk, Load, CLEAR:IN std_logic;
		D:		 						IN std_logic_vector(3 downto 0);
		TxD, KbFree:				OUT std_logic;
		state:					OUT std_logic_vector(6 downto 0)
	);
END KeyTransmitter;

ARCHITECTURE Behaviour OF KeyTransmitter IS
	
	component ShiftRegisterL7
		PORT(
			CLK, CE, PL, CLEAR: 	IN std_logic;
			D: 						IN std_logic_vector(3 downto 0);
			Q, zeros:				OUT std_logic;
			state:					OUT std_logic_vector(6 downto 0)
		);
	end component;
	
	component KeyTransmitterControl
		PORT(
			clk_in, Load, CLEAR, CE, zeros: IN std_logic;
			kbFree, startSignal, shiftEnable, PL: OUT std_logic
		);
	end component;
	
	component MUX2_1L1
		PORT(
			A,B: IN std_logic;
			S: IN std_logic;
			Y: OUT std_logic
		);
	end component;
	
	signal shiftClk, PL, errorZeros, transZero, startSignal, shiftEnable, shiftBit, signalFree: std_logic;
	signal countValues, bufferK: std_logic_vector(3 downto 0);
	
BEGIN

--	shiftClk <= CLK when shiftEnable = '0' else TxClk;

	
	clkMux: MUX2_1L1 port map(
		A 		=> CLK,
		B		=> TxClk,
		S		=> shiftEnable,
		Y		=> shiftClk 
	);
	
	shiftRegister1: ShiftRegisterL7 port map(
		CLK 		=> shiftClk,
		CE 		=> shiftEnable,
		PL 		=> PL,			
		CLEAR		=> CLEAR,
		D			=>	D,
		Q 			=> shiftBit,
		zeros		=> errorZeros,
		state		=> state
	);

	
	control: KeyTransmitterControl port map(
		clk_in 		=> CLK,
		CE 			=> '1',
		Load 			=> Load,
		zeros			=> errorZeros,
		CLEAR			=> CLEAR,
		kbFree 		=> signalFree,
		startSignal => startSignal,
		shiftEnable	=> shiftEnable,
		PL				=> PL
	);
	
	KbFree <= signalFree;
	
	--TxD	<=	'1' when signalFree = '1' else
	--			'0' when startSignal = '1' else
	--			shiftBit when shiftEnable = '1' else '1';
				
	TxD	<=	'1' when signalFree = '1' else
				'0' when startSignal = '1' else
				shiftBit when shiftEnable = '1' else '1';
	
	

END Behaviour;