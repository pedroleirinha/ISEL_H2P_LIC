library ieee;
use ieee.std_logic_1164.all;

ENTITY ShiftRegisterL7 IS
	PORT(
		CLK, CE, PL, CLEAR: 	IN std_logic;
		D: 						IN std_logic_vector(3 downto 0);
		Q, zeros:				OUT std_logic;
		state:					OUT std_logic_vector(6 downto 0)
	);
END ShiftRegisterL7;

ARCHITECTURE Behaviour OF ShiftRegisterL7 IS
	
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	component RegistryL3
		PORT(	
			D: IN std_logic_vector (2 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (2 downto 0)
		);
	end component;
	
	component MUX2_1L7
		PORT(
			A,B: IN std_logic_vector (6 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (6 downto 0)
		);
	end component;

	signal regCE: std_logic;
	signal currRegState, nextRegState, registryD, inputValueKey: std_logic_vector(6 downto 0);
	
		
BEGIN

	state	<= currRegState;
	regCE <= CE OR PL;
	
	registry1: RegistryL4 port map(
		clk_in => CLK,
		CLEAR  => CLEAR,
		D => registryD(3 downto 0),
		CE => regCE,
		Q => currRegState(3 downto 0)
	);

	registry2: RegistryL3 port map(
		clk_in => CLK,
		CLEAR  => CLEAR,
		D => registryD(6 downto 4),
		CE => regCE,
		Q => currRegState(6 downto 4)
	);
	
	
	inputValueKey <= '1' & '0' & D(3 downto 0) & '1';
	
	mux: MUX2_1L7 port map(
		A	=>	nextRegState, 	
		B	=>	inputValueKey, 
		S	=>	PL,
		Y	=>	registryD
	);
	
	Q 		<= currRegState(0);
	zeros <= NOT currRegState(0) AND NOT currRegState(1) AND NOT currRegState(2) AND 
				NOT currRegState(3) AND NOT currRegState(4) AND NOT currRegState(5) AND 
				NOT currRegState(6);

	process(currRegState)
	begin
		nextRegState(5 downto 0) <= currRegState(6 downto 1);
		nextRegState(6) <= '0';
	end process;

	

END Behaviour;