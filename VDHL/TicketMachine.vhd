library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		CLK, CLEAR:								IN std_logic;
		KEYPAD_LIN: 							IN std_logic_vector(3 downto 0);
		LCD_DATA:		 						OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, Kval:				OUT std_logic; 
		KEYPAD_COL: 							OUT std_logic_vector(3 downto 0);
		K: 										OUT std_logic_vector (3 downto 0)
	);
	
END TicketMachine;

ARCHITECTURE Behaviour OF TicketMachine IS

	component CLKDIV
		port ( 
			clk_in: in std_logic;
			clk_out: out std_logic
		);
	end component;

	component KeyboardReader
		PORT(
			clk_in, Kack, CLEAR, TxClk: 	IN std_logic;
			rows: 								IN std_logic_vector(3 downto 0);
			cols: 								OUT std_logic_vector(3 downto 0);
			K: 									OUT std_logic_vector (3 downto 0);
			Kval, TxD:							OUT std_logic
		);
	end component;
	

	component UsbPort 
		PORT
		(
			inputPort:  	IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
			outputPort:		OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
		);
	end component;
	
	
	component PortExpanderLCD                           
		PORT(
			SCLK, SDX, SS, CLEAR: 	IN std_logic;
			Q: 							OUT std_logic_vector(9 downto 0)
		);
	end component;
	
		
	component PortExpanderTD
		PORT(
			SCLK, SDX, SS, CLEAR: 	IN std_logic;
			Q: 							OUT std_logic_vector(9 downto 0)
		);
	end component;
	
	signal input, output: 	STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal QLCD, QTD :					STD_LOGIC_VECTOR(9 DOWNTO 0);
	signal values: 			STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal clock, Kval_Decode, SDX, Kack, SS, SCLK, TxClk_i, TxD_o: STD_LOGIC;
	
BEGIN

	clock1: CLKDIV port map(
		clk_in 	=> CLK,
		clk_out	=> clock
	);
		
	peLcd: PortExpanderLCD port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS,
		Q 		=>	QLCD
	);
	
	peTd: PortExpanderTD port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS,
		Q 		=>	QTD
	);
	
	keyboardReader1: KeyboardReader port map(
		clk_in 	=> clock,
		TxClk		=> TxClk_i,
		Kack 		=> Kack,
		CLEAR 	=> CLEAR,
		rows 		=> KEYPAD_LIN,		
		cols 		=> KEYPAD_COL,	
		K 			=> values,		
		Kval 		=>	Kval_Decode,
		TxD		=> TxD_o
	);
	
		
-- input <= Kval_Decode & "000" & values;
	input <= Kval_Decode & "000000" & TxD_o;
	
	Kval <= Kval_Decode;
	
	UsbPort1: UsbPort port map(
		inputPort	=> input,
		outputPort	=> output
	);
	
	
	TxClk_i <= output(1);
	
	Kack  <= output(7);
	
	SDX 	<= output(0);
	SCLK	<= output(1);
	SS		<= output(2);
	
	K <= values;
	
	LCD_EN <= QLCD(9);
	LCD_DATA <= QLCD(8 downto 1);
	LCD_RS <= QLCD(0);
	

END Behaviour;