library ieee;
use ieee.std_logic_1164.all;

ENTITY TicketMachine IS
	PORT(
		clk_in, CLEAR: 	IN std_logic;
		K: 					OUT std_logic_vector (3 downto 0)
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
			inputPort:  IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
			outputPort :  OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
		);
	end component;
	
	signal input, output: STD_LOGIC_VECTOR(7 DOWNTO 0);
	signal rows, cols, values: STD_LOGIC_VECTOR(3 DOWNTO 0);
	signal Kval, Kack: STD_LOGIC;
	
BEGIN
	
	Kack <= output(0);
	
	decode: KeyDecode port map(
		clk_in 	=> clk_in,
		Kack 	=> Kack,
		CLEAR 	=> CLEAR,
		rows 		=> rows,		
		cols 		=> cols,	
		K 			=> values,		
		Kval 	=>	Kval
	);
	
	input <= "11111111" when (Kval = '0') else "0000" & values;
	
	UsbPort1: UsbPort port map(
		inputPort	=> input,
		outputPort	=> output
	);
	
	K <= values;
	
	

END Behaviour;