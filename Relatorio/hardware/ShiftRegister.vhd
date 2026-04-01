library ieee;
use ieee.std_logic_1164.all;

ENTITY ShiftRegister IS
	PORT(
		SCLK, enableShift, SerialIn, CLEAR: 	IN std_logic;
		Q: 												OUT std_logic_vector(9 downto 0)
	);
END ShiftRegister;

ARCHITECTURE Behaviour OF ShiftRegister IS
	
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
	
	signal D, D_NEXT: std_logic_vector(9 downto 0);
	signal registerEnable: std_logic;
	
		
BEGIN

	registerEnable <= NOT enableShift;
	
	registry1: RegistryL4 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D_NEXT(3 downto 0),
		CE => registerEnable,
		Q => D(3 downto 0)
	);
	
	registry2: RegistryL4 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D_NEXT(7 downto 4),
		CE => registerEnable,
		Q => D(7 downto 4)
	);
	
	registry3: RegistryL2 port map(
		clk_in => SCLK,
		CLEAR  => CLEAR,
		D => D_NEXT(9 downto 8),
		CE => registerEnable,
		Q => D(9 downto 8)
	);
	
	Q <= D;
	
	process(D_NEXT, D, SerialIn)
	begin
	  
	  	
    D_NEXT(9 downto 1) <= D(8 downto 0);
    D_NEXT(0) <= SerialIn;
		
	end process;

END Behaviour;