library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyboardReader IS
	PORT(
		clk_in, CLEAR, TxClk: 			IN std_logic;
		rows: 								IN std_logic_vector(3 downto 0);
		delay:					         IN std_logic_vector(1 downto 0); 
		cols: 								OUT std_logic_vector(3 downto 0);
		K: 									OUT std_logic_vector (3 downto 0);
		Kval, TxD, KbFree:				OUT std_logic;
		
		putIndex, getIndex: 				OUT std_logic_vector(3 downto 0);
		Wreg_out, DAC_out:				OUT std_logic;
		emptySignal_out, inv_fullSignal:	OUT std_logic;
		DAV, CTS, Kpress:					OUT std_logic
		
	);
END KeyboardReader;

ARCHITECTURE Behaviour OF KeyboardReader IS

	component CLKDIV	
		port ( 
			clk_in: in std_logic;
			clk_out: out std_logic
		);
	end component;
	
	component KeyDecode
		PORT(
			clk_in, Kack, CLEAR: 	IN std_logic;
			rows: 						IN std_logic_vector(3 downto 0);
			delay:					   IN std_logic_vector(1 downto 0);
			cols: 						OUT std_logic_vector(3 downto 0);
			K: 							OUT std_logic_vector (3 downto 0);
			Kval:							OUT std_logic;
			
			Kpress:						OUT std_logic
		);
	end component;

	component RingBuffer
		PORT(
			clk_in, DAV, CTS, CLEAR: 	IN std_logic;
			D: 								IN std_logic_vector(3 downto 0);
			Q: 								OUT std_logic_vector (3 downto 0);
			Wreg, DAC:						OUT std_logic;
			
		   putIndex, getIndex: 			OUT std_logic_vector(3 downto 0);
			emptySignal_out, fullSignal_out:	OUT std_logic
		);
	end component;
	
	component KeyTransmitter
		PORT(
			CLK, TxClk, Load, CLEAR:IN std_logic;
			D:		 						IN std_logic_vector(3 downto 0);
			TxD, KbFree:				OUT std_logic
		);
	end component;
	
	signal DAC, Wreg, KbFreeSignal, Kvalue, clock: std_logic;
	signal fullSignal2: std_logic;
	
	signal bufferD, ringQ: std_logic_vector(3 downto 0);
	
BEGIN


	clock1: CLKDIV port map(
		clk_in 	=> clk_in,
		clk_out	=> clock
	);
	
	scan: KeyDecode port map(
		clk_in 	=> clock,
		Kack 		=> DAC,
		delay		=> delay,
		CLEAR 	=> CLEAR,
		rows 		=> rows,		
		cols 		=> cols,	
		K 			=> bufferD,		
		Kval 		=>	Kvalue,
		
		Kpress   => Kpress
	);	
	
	transmitter: KeyTransmitter port map(
		CLK		=> clk_in,
		TxClk 	=> TxClk,
		Load 		=> Wreg,
		CLEAR 	=> CLEAR,
		D 			=> ringQ,
		TxD 		=> TxD,
		KbFree 	=> KbFreeSignal
	);
	
	KbFree 	<= KbFreeSignal;
		
   ringBuffer1: RingBuffer port map(
		clk_in 	=> clk_in,
		
		--clk_in 	=> clock, -- 2026.05.26
		
		
		DAV 		=> Kvalue,
		CTS 		=> KbFreeSignal,
		CLEAR 	=> CLEAR,
		D 			=> bufferD,
		Q 			=> ringQ,
		Wreg 		=>	Wreg,
		DAC		=> DAC,
		
		putIndex    => putIndex,
		getIndex    => getIndex,
		emptySignal_out => emptySignal_out,
		fullSignal_out  => fullSignal2
	);
	
	inv_fullSignal <= not (fullSignal2);
	
	
	Wreg_out <= Wreg;
	DAC_out  <= DAC;
	
	K 		<= bufferD;
	Kval 	<= Kvalue;
	
	DAV   <= Kvalue;
	CTS   <= KbFreeSignal;


END Behaviour;