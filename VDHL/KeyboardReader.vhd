library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyboardReader IS
	PORT(
		clk_in, CLEAR, TxClk: 	IN std_logic;
		rows: 								IN std_logic_vector(3 downto 0);
		cols: 								OUT std_logic_vector(3 downto 0);
		K: 									OUT std_logic_vector (3 downto 0);
		Kval, TxD, KbFree:				OUT std_logic;
		state:								OUT std_logic_vector(6 downto 0)
	);
END KeyboardReader;

ARCHITECTURE Behaviour OF KeyboardReader IS
	component KeyDecode
		PORT(
			clk_in, Kack, CLEAR: 	IN std_logic;
			rows: 						IN std_logic_vector(3 downto 0);
			cols: 						OUT std_logic_vector(3 downto 0);
			K: 							OUT std_logic_vector (3 downto 0);
			Kval:							OUT std_logic
		);
	end component;

	component RingBuffer
		PORT(
			clk_in, DAV, CTS, CLEAR: 	IN std_logic;
			D: 								IN std_logic_vector(3 downto 0);
			Q: 								OUT std_logic_vector (3 downto 0);
			Wreg, DAC:						OUT std_logic
		);
	end component;
	
	component KeyTransmitter
		PORT(
			CLK, TxClk, Load, CLEAR:IN std_logic;
			D:		 						IN std_logic_vector(3 downto 0);
			TxD, KbFree:				OUT std_logic;
			state:					OUT std_logic_vector(6 downto 0)
		);
	end component;
	
	signal DAV, CTS, DAC, Wreg, KbFreeSignal, Kvalue: std_logic;
	signal bufferD: std_logic_vector(3 downto 0);
	
BEGIN
	
	scan: KeyDecode port map(
		clk_in 	=> clk_in,
		Kack 		=> NOT KbFreeSignal,
		CLEAR 	=> CLEAR,
		rows 		=> rows,		
		cols 		=> cols,	
		K 			=> bufferD,		
		Kval 		=>	Kvalue
	);	
	
	transmitter: KeyTransmitter port map(
		CLK		=> clk_in,
		TxClk 	=> TxClk,
		Load 		=> Kvalue,
		CLEAR 	=> CLEAR,
		D 			=> bufferD,		
		TxD 		=> TxD,	
		KbFree 	=> KbFreeSignal,
		state 	=> state
	);
	
	KbFree <= KbFreeSignal;
	
		
--	ringBuffer1: RingBuffer port map(
--		clk_in 	=> clk_in,
--		DAV 		=> DAV,
--		CTS 		=> CTS,
--		CLEAR 	=> CLEAR,		
--		D 			=> bufferD,	
--		Q 			=> K,		
--		Wreg 		=>	Wreg,
--		DAC		=> DAC
--	);
	
	K 		<= bufferD;
	Kval 	<= Kvalue;


END Behaviour;