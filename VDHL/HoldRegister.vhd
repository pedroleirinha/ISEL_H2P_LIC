library ieee;
use ieee.std_logic_1164.all;

ENTITY HoldRegister IS
	PORT(
		SCLK, CLEAR	: 	IN std_logic;
		D:					IN std_logic_vector(9 downto 0);
		Q:					OUT std_logic_vector(9 downto 0)
	);
END HoldRegister;

ARCHITECTURE Behaviour OF HoldRegister IS
	
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;

	component RegistryL2 
		PORT(	
			D: IN std_logic_vector (1 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (1 downto 0)
		);
	end component;

BEGIN
	
	registry1: RegistryL4 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D(3 downto 0),
		CE => '1',
		Q => Q(3 downto 0)
	);
	
	registry2: RegistryL4 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D(7 downto 4),
		CE => '1',
		Q => Q(7 downto 4)
	);
	
	registry3: RegistryL2 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D(9 downto 8),
		CE => '1',
		Q => Q(9 downto 8)
	);
	
	

END Behaviour;