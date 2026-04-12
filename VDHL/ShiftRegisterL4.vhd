library ieee;
use ieee.std_logic_1164.all;

ENTITY ShiftRegisterL4 IS
	PORT(
		CLK, CE, PL, CLEAR: 	IN std_logic;
		D: 						IN std_logic_vector(3 downto 0);
		Q:							OUT std_logic
	);
END ShiftRegisterL4;

ARCHITECTURE Behaviour OF ShiftRegisterL4 IS
	
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	component MUX2_1L4
		PORT(
			A,B: IN std_logic_vector (3 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (3 downto 0)
		);
	end component;

	
	signal currRegState, nextRegState, registryD: std_logic_vector(3 downto 0);
	
		
BEGIN

	
	registry1: RegistryL4 port map(
		clk_in => CLK,
		CLEAR  => CLEAR,
		D => registryD(3 downto 0),
		CE => CE,
		Q => currRegState(3 downto 0)
	);
	
	mux4: MUX2_1L4 port map(
		A	=>	nextRegState, 	
		B	=>	D,
		S	=>	PL,
		Y	=>	registryD
	);
	
	Q <= currRegState(0);
	
	process(nextRegState, currRegState)
	begin
	  
	  	
    nextRegState(2 downto 0) <= currRegState(3 downto 1);
    nextRegState(3) <= '0';
		
	end process;

END Behaviour;