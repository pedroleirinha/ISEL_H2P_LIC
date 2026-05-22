library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyTransmitter IS
	PORT(
		CLK ,TxClk, Load, CLEAR:IN std_logic;
		D:		 						IN std_logic_vector(3 downto 0);
		TxD, KbFree:				OUT std_logic
	);
END KeyTransmitter;

ARCHITECTURE Behaviour OF KeyTransmitter IS
	
	component ShiftRegisterL6
		PORT(
			CLK, CE, PL, CLEAR: 	IN std_logic;
			D: 						IN std_logic_vector(6 downto 0);
			Q, zeros:				OUT std_logic;
			state:					OUT std_logic_vector(6 downto 0)
		);
	end component;
	
	component KeyTransmitterControl
		PORT(
			clk_in, Load, CLEAR, CE, zeros: IN std_logic;
			kbFree, shiftEnable, PL: OUT std_logic
		);
	end component;
	
	component MUX2_1L1
		PORT(
			A,B: IN std_logic;
			S: IN std_logic;
			Y: OUT std_logic
		);
	end component;
	
	signal InvClk, shiftClk, PL, errorZeros, shiftEnable, shiftBit, TxDFinal: std_logic;
	signal shiftRegisterBits: std_logic_vector(6 downto 0);
	
BEGIN
	InvClk <= NOT CLK;

	clkMux: MUX2_1L1 port map(
		A 		=> TxClk,
		B		=> InvClk,
		S		=> PL,
		Y		=> shiftClk 
	);
	
	shiftRegister1: ShiftRegisterL6 port map(
		CLK 		=> shiftClk,
		CE 		=> shiftEnable,
		PL 		=> PL,			
		CLEAR		=> CLEAR,
		D			=>	shiftRegisterBits,
		Q 			=> shiftBit,
		zeros		=> errorZeros
	);
	
	
	shiftRegisterBits <= '0' & D(3 downto 0) & '1' & '0';

	
	control: KeyTransmitterControl port map(
		clk_in 		=> CLK,
		CE 			=> '1',
		Load 			=> Load,
		zeros			=> errorZeros,
		CLEAR			=> CLEAR,
		kbFree 		=> KbFree,
		shiftEnable	=> shiftEnable,
		PL				=> PL
	);
		
	
	TxD <= shiftBit;
	
	

END Behaviour;