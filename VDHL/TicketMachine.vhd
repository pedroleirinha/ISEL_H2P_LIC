library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		CLOCK_50, CLEAR, CollectTicket, Coin, Manut:		IN std_logic;
		KEYPAD_LIN:									IN std_logic_vector(3 downto 0);
      delay:					               IN std_logic_vector(1 downto 0); 
		COINS: 										IN std_logic_vector(2 downto 0);

		
		
		LCD_DATA:		 							OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, CoinAccepted, Prt:	OUT std_logic; 
		KEYPAD_COL: 								OUT std_logic_vector(3 downto 0);
		K: 											OUT std_logic_vector(3 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: OUT STD_LOGIC_VECTOR(7 downto 0);
		state:										OUT std_logic_vector(7 downto 0);
		s_time_up:  								OUT std_logic;
		
		putIndex, getIndex: 						OUT std_logic_vector(3 downto 0);
		Wreg, DAC, DAV, CTS:						OUT std_logic;
		emptySignal, fullSignal:	   		OUT std_logic;
		Kval:								 			OUT std_logic;
		Kpress:										OUT std_logic;
		
		rows:											out std_logic_vector(3 downto 0)
	);
	
END TicketMachine;

ARCHITECTURE Behaviour OF TicketMachine IS


	component KeyboardReader
		PORT(
			clk_in, CLEAR, TxClk:		 	IN std_logic;
			delay:               			IN std_logic_vector(1 downto 0); 
			rows: 								IN std_logic_vector(3 downto 0);
			cols: 								OUT std_logic_vector(3 downto 0);
			K: 									OUT std_logic_vector (3 downto 0);
			TxD, Kval:							OUT std_logic;
			
			putIndex, getIndex: 				OUT std_logic_vector(3 downto 0);
			Wreg_out, DAC_out, DAV, CTS:	OUT std_logic;
			emptySignal_out, inv_fullSignal:	   OUT std_logic;
			Kpress:                       OUT std_logic
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
	
	signal input, output:		STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal values: 				STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal clock: 					STD_LOGIC;
	
	
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
	signal coinAccepted2, coinsCollected, ejectCoins: STD_LOGIC;
	

	component Time_Delay
	  PORT(
        clk, ceTimer, resetTimer: 	IN  std_logic;                                 
        delays: 							IN  std_logic_vector(1 downto 0); 
        timeUp: 							OUT std_logic	 
	  );
	end component;

	
	
BEGIN

--##################

	time_D: Time_Delay port map(
	  clk         => clock_50,
	  ceTimer     => '1',
	  resetTimer  => '0',
	  delays 	  => delay,          
	  timeUp      => s_time_up           
	);
--##################


		
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
		clk_in 	=> CLOCK_50,
		delay		=> delay,
		TxClk		=> TxClk_i,
		CLEAR 	=> CLEAR,
		rows 		=> KEYPAD_LIN,		
		cols 		=> KEYPAD_COL,	
		K 			=> values,		
		TxD		=> TxD_o,
		Kval     => Kval,
		
		
		putIndex => putIndex,
		getIndex => getIndex,
		Wreg_out => Wreg,
		 DAC_out => DAC,
			  DAV => DAV,
			  CTS => CTS,
	emptySignal_out => emptySignal,
	 inv_fullSignal	=> fullSignal,
	     Kpress => Kpress
	);
	
	rows <= KEYPAD_LIN;
	
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

	UsbPort1: UsbPort port map(
		inputPort	=> input,
		outputPort	=> output
	);
	
	input <= TxD_o & Manut &"0" & fnFlag & coin & coins;
	Prt 	<= PrtFlag;
	
	state <= input;
	
	-- Info for TicketDispenser
	PrtFlag			<= QTD(9);
	roundtripFlag	<= QTD(0);
	destStation 	<= QTD(4 downto 1);
	origStation 	<= QTD(8 downto 5);

		

	-- Info for CoinAcceptor
	coinAccepted2 	<= output(4);
	coinsCollected <= output(6);
	ejectCoins		<= output(5);
	
	CoinAccepted <= coinAccepted2;
	
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