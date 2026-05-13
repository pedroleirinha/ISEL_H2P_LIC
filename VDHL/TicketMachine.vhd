library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		CLK, CLEAR, CollectTicket, Coin:		IN std_logic;
		KEYPAD_LIN: 								IN std_logic_vector(3 downto 0);
		COINS: 										IN std_logic_vector(2 downto 0);
		--output:									IN std_logic_vector(7 downto 0);
		LCD_DATA:		 							OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, txD, KbFree:			OUT std_logic; 
		KEYPAD_COL: 								OUT std_logic_vector(3 downto 0);
		K: 											OUT std_logic_vector(3 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: OUT STD_LOGIC_VECTOR(7 downto 0);
		state:										OUT std_logic_vector(7 downto 0)
	);
	
END TicketMachine;

ARCHITECTURE Behaviour OF TicketMachine IS


	component KeyboardReader
		PORT(
			clk_in, CLEAR, TxClk:		 	IN std_logic;
			rows: 								IN std_logic_vector(3 downto 0);
			cols: 								OUT std_logic_vector(3 downto 0);
			K: 									OUT std_logic_vector (3 downto 0);
			Kval, TxD, KbFree:				OUT std_logic;
			state:								OUT std_logic_vector(7 downto 0)
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
	
	component TICKET_DISPENSER
		PORT(
			RT, Prt, CollectTicket: in STD_LOGIC;
			O, D: in STD_LOGIC_VECTOR(3 downto 0);
			Fn: out STD_LOGIC;
			HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: out STD_LOGIC_VECTOR(7 downto 0)
		);
	end component;
	
	component CoinAcceptor
		PORT (
			accept, collect, eject: in STD_LOGIC;
			Coins: out STD_LOGIC_VECTOR(2 downto 0);
			Coin: out STD_LOGIC
		);
	end component;
	
	signal input, output:		STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal values: 				STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal clock, Kval_Decode: STD_LOGIC;
	
	
	-- Signals for LCD
	signal QLCD, QTD: 		STD_LOGIC_VECTOR(9 DOWNTO 0);
	signal SS_LCD: 			STD_LOGIC;
	
	-- Signals for KeyboardReader
	signal TxClk_i, TxD_o: STD_LOGIC;
	
	-- Signals for TicketDispenser
	signal origStation, destStation: 				 STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal SCLK, SDX, SS_TD: STD_LOGIC;
	signal fnFlag, roundtripFlag, PrtFlag, collectFlag: STD_LOGIC;
	
	-- Signals for CoinAcceptor
	signal coinAccepted, coinsCollected, ejectCoins: STD_LOGIC;
	signal Coin2: STD_LOGIC;
	signal Coins2: STD_LOGIC_VECTOR(2 DOWNTO 0);
	
	
BEGIN

		
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
		clk_in 	=> CLK,
		TxClk		=> TxClk_i,
		CLEAR 	=> CLEAR,
		rows 		=> KEYPAD_LIN,		
		cols 		=> KEYPAD_COL,	
		K 			=> values,		
		Kval 		=>	Kval_Decode,
		TxD		=> TxD_o,
		KbFree	=> KbFree,
		state		=> state
	);
	
	ticketDispenser: TICKET_DISPENSER port map(
		Prt 				=> PrtFlag,
		CollectTicket  => CollectTicket,
		D 	   			=> destStation,
		O 	   			=> origStation,
		RT					=>	roundtripFlag,
		Fn  				=> fnFlag,
		HEX0    			=> HEX0,
		HEX1    			=> HEX1,
		HEX2    			=> HEX2,
		HEX3    			=> HEX3,
		HEX4    			=> HEX4,
		HEX5    			=> HEX5
	);
	
	coinsAcc: CoinAcceptor port map(
		accept			=> coinAccepted, 
		collect			=> coinsCollected, 
		eject				=> ejectCoins,
		Coins				=> Coins2,
		Coin				=> Coin2
	);
	
	UsbPort1: UsbPort port map(
		inputPort	=> input,
		outputPort	=> output
	);
	
	--input <= Kval_Decode & "000000" & TxD_o;
   --input <= Kval_Decode & "000" & values;
	input <= TxD_o & "000" & coin & coins;
	
	txD	<= TxD_o;
	
	-- Info for TicketDispenser
	PrtFlag			<= QTD(9);
	roundtripFlag	<= QTD(0);
	destStation 	<= QTD(4 downto 1);
	origStation 	<= QTD(8 downto 5);

		

	-- Info for CoinAcceptor
	coinAccepted 	<= output(4);
	coinsCollected <= output(6);
	ejectCoins		<= output(5);
	
	
	-- Info for KeyTransmitter
	TxClk_i 	<= output(7);
	
	
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