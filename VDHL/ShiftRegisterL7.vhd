library ieee;
use ieee.std_logic_1164.all;

ENTITY ShiftRegisterL7 IS
	PORT(
		CLK, CE, PL, CLEAR: 	IN std_logic;
		D: 						IN std_logic_vector(7 downto 0);
		Q, zeros:				OUT std_logic;
		state:					OUT std_logic_vector(7 downto 0)
	);
END ShiftRegisterL7;

ARCHITECTURE Behaviour OF ShiftRegisterL7 IS
	
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, SET, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	

	component MUX2_1L8
		PORT(
			A,B: IN std_logic_vector (7 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (7 downto 0)
		);
	end component;

	signal regCE: std_logic;
	signal currRegState, nextRegState, registryD: std_logic_vector(7 downto 0);
	
		
BEGIN

	state	<= currRegState;
	
	regCE <= CE OR PL;
	
	registry1: RegistryL4 port map(
		clk_in => CLK,
		SET  => CLEAR,
		CLEAR => '0',
		D => registryD(3 downto 0),
		CE => regCE,
		Q => currRegState(3 downto 0)
	);

	registry2: RegistryL4 port map(
		clk_in => CLK,
		SET  => CLEAR,
		CLEAR => '0',
		D => registryD(7 downto 4),
		CE => regCE,
		Q => currRegState(7 downto 4)
	);
	
	
	mux: MUX2_1L8 port map(
		A	=>	nextRegState, 	
		B	=>	D, 
		S	=>	PL,
		Y	=>	registryD
	);
	
	Q 		<= currRegState(0);
	zeros <= currRegState(0) AND currRegState(1) AND currRegState(2) AND 
				currRegState(3) AND currRegState(4) AND currRegState(5) AND 
				currRegState(6) AND currRegState(7);

	nextRegState(6 downto 0) <= currRegState(7 downto 1);
	nextRegState(7) <= '1';

	

END Behaviour;