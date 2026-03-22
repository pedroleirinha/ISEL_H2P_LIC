library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		clk_in, CLEAR:							IN std_logic;
		KEYPAD_LIN: 							IN std_logic_vector(3 downto 0);
		LCD_DATA:		 						OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS:						OUT std_logic;
		KEYPAD_COL: 							OUT std_logic_vector(3 downto 0);
		K: 										OUT std_logic_vector (3 downto 0)
	);
END TicketMachine;

ARCHITECTURE Behaviour OF TicketMachine IS

	component KeyDecode
		PORT(
			clk_in, Kack, CLEAR: 	IN std_logic;
			rows: 						IN std_logic_vector(3 downto 0);
			cols: 						OUT std_logic_vector(3 downto 0);
			K: 							OUT std_logic_vector (3 downto 0);
			Kval:							OUT std_logic
		);
	end component;
	

	component UsbPort
		PORT
		(
			inputPort:  	IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
			outputPort:		OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
		);
	end component;
	
	
	component SerialReceiver                           
		PORT(
			SCLK, SDX, SS, CLEAR: 	IN std_logic;
			Q: 							OUT std_logic_vector(9 downto 0)
		);
	end component;
	
	signal input, output: STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal Q: STD_LOGIC_VECTOR(9 DOWNTO 0);
	signal rows, cols, values: STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal Kval, Kack, SDX, SS, SCLK: STD_LOGIC;
	
BEGIN
		
	serialReceiver1: SerialReceiver port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS,
		Q 		=>	Q
	);
	
	decode: KeyDecode port map(
		clk_in 	=> clk_in,
		Kack 		=> Kack,
		CLEAR 	=> CLEAR,
		rows 		=> rows,		
		cols 		=> cols,	
		K 			=> values,		
		Kval 		=>	Kval
	);
	
	input <= "00000000" when (Kval = '0') else "000" & Kval & values;
	
	UsbPort1: UsbPort port map(
		inputPort	=> input,
		outputPort	=> output
	);
	
	Kack  <= output(0);
	SDX 	<= output(0);
	SCLK	<= output(1);
	SS		<= output(2);
	
	K <= values;
	
	LCD_EN <= Q(9);
	LCD_DATA <= Q(8 downto 1);
	LCD_RS <= Q(0);
	
	

END Behaviour;