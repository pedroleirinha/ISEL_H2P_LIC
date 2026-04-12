library ieee;
use ieee.std_logic_1164.all;

ENTITY MemoryAddressControl IS
	PORT(
		clk_in, putGet, CLEAR, incPut, incGet: IN std_logic;
		Q:		 											OUT std_logic_vector(3 downto 0);
		full, empty:									OUT std_logic
	);
END MemoryAddressControl;

ARCHITECTURE Behaviour OF MemoryAddressControl IS

	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	signal putIndex, getIndex: std_logic_vector(3 downto 0);
	
BEGIN
	
	
	putReg: RegistryL4 port map(
		clk_in => clk_in,
		CLEAR  => CLEAR,
		D => putIndex(3 downto 0),
		CE => putGet,
		Q => putIndex(3 downto 0)
	);
	
	
	getReg: RegistryL4 port map(
		clk_in => clk_in,
		CLEAR  => CLEAR,
		D => getIndex(3 downto 0),
		CE => NOT putGet,
		Q => getIndex(3 downto 0)
	);
	
	
	

END Behaviour;