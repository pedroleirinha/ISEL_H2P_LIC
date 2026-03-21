library ieee;
use ieee.std_logic_1164.all;

ENTITY RegistryL2 IS
	PORT(
		D: IN std_logic_vector (1 downto 0);
		clk_in, CE, CLEAR: IN std_logic;
		Q: OUT std_logic_vector (1 downto 0)
	);
END RegistryL2;

ARCHITECTURE Structural OF RegistryL2 IS
	component FFD 
		PORT(	
			CLK : in std_logic;
			RESET : in STD_LOGIC;
			SET : in std_logic;
			D : IN STD_LOGIC;
			EN : IN STD_LOGIC;
			Q : out std_logic
		);
	end component;
	
	signal flipflopsCurrentState: std_logic_vector (1 downto 0);
	
BEGIN	
	
	flipflop1: FFD port map(
		CLK => clk_in,
		RESET  => CLEAR,
		SET => '0',
		D => D(0),
		EN => CE,
		Q => flipflopsCurrentState(0)
	);
	
	flipflop2: FFD port map(
		CLK => clk_in,
		RESET  => CLEAR,
		SET => '0',
		D => D(1),
		EN => CE,
		Q => flipflopsCurrentState(1)
	);

	
	Q <= flipflopsCurrentState;
	
END Structural;