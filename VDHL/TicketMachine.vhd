library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		CLK, CLEAR:									IN std_logic;
		KEYPAD_LIN: 								IN std_logic_vector(3 downto 0);
		LCD_DATA:		 							OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, Kval:					OUT std_logic; 
		KEYPAD_COL: 								OUT std_logic_vector(3 downto 0);
		K: 											OUT std_logic_vector (3 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: OUT STD_LOGIC_VECTOR(7 downto 0) 
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
	

--	component UsbPort 
--		PORT
--		(
--			inputPort:  	IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
--			outputPort:		OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
--		);
--	end component;
	
	
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
	
	component TICKET_DISPENSER
		PORT(
			Prt, CollectTicket: in STD_LOGIC;
			Dout: in STD_LOGIC_VECTOR(8 downto 0);
			Fn: out STD_LOGIC;
			HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: out STD_LOGIC_VECTOR(7 downto 0) 
		);
	end component;
	
	signal input, output: 	STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal QLCD, QTD :					STD_LOGIC_VECTOR(9 DOWNTO 0);
	signal values, origStation, destStation: 			STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal clock, Kval_Decode, Kack, SCLK, SDX, SS_LCD, SS_TD, TxClk_i, TxD_o: STD_LOGIC;
	
	signal TDout: STD_LOGIC_VECTOR(8 downto 0);
	signal fnFlag, roundtripFlag, PrtFlag, collectFlag: STD_LOGIC;
	
BEGIN

	clock1: CLKDIV port map(
		clk_in 	=> CLK,
		clk_out	=> clock
	);
		
	peLcd: PortExpanderLCD port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS_LCD,
		Q 		=>	QLCD
	);
	
	peTd: PortExpanderTD port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS_TD,
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
	
	
	ticketDispenser: TICKET_DISPENSER port map(
		Prt 				=> PrtFlag,
		CollectTicket  => collectFlag,
		Dout    			=> TDout,
		Fn  				=> fnFlag,
		HEX0    			=> HEX0,
		HEX1    			=> HEX1,
		HEX2    			=> HEX2,
		HEX3    			=> HEX3,
		HEX4    			=> HEX4,
		HEX5    			=> HEX5
	);
	
	
	
	TDout <= destStation & origStation & roundtripFlag;
		
--	UsbPort1: UsbPort port map(
--		inputPort	=> input,
--		outputPort	=> output
--	);
	
	input <= Kval_Decode & "000000" & TxD_o;
-- input <= Kval_Decode & "000" & values;
	
	
	-- Info for TicketDispenser
	PrtFlag			<= QTD(9);
	collectFlag 	<= output(6);
	roundtripFlag	<= QTD(0);
	destStation 	<= QTD(4 downto 1);
	origStation 	<= QTD(8 downto 5);
	
	
	-- Info for Key detection
	Kval 		<= Kval_Decode;
	Kack  	<= output(7);
	

	-- Info for KeyTransmitter
	TxClk_i 	<= output(1);
	
	
	-- Info for Serial Receiver
	SDX 	<= output(0);
	SCLK	<= output(1);
	SS_LCD	<= output(2);
	SS_TD		<= output(3);
	
	-- Info for LCD
	LCD_EN <= QLCD(9);
	LCD_DATA <= QLCD(8 downto 1);
	LCD_RS <= QLCD(0);
	
	K <= values;

END Behaviour;