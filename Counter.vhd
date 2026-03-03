library ieee;
use ieee.std_logic_1164.all;

ENTITY Counter IS
	PORT(
		clk_in, CE, CLEAR, PL: IN std_logic;
		initial, step: IN std_logic_vector (3 downto 0);
		Q: OUT std_logic_vector (3 downto 0);
		Z: OUT std_logic
	);
END Counter;

ARCHITECTURE Structural OF Counter IS
	component RegistryL4 
		PORT(	
			D: IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	component Adder
		PORT(	
			A,B: IN std_logic_vector (3 downto 0);
			C0: IN std_logic;
			S: OUT std_logic_vector (3 downto 0);
			C4: OUT std_logic
		);
	end component;
	
	component MUX2_1L4
		PORT(
			A,B: IN std_logic_vector (3 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	signal flipflopsCurrentState: std_logic_vector (3 downto 0);
	signal flipflopsNextState: std_logic_vector (3 downto 0);
	signal addRes: std_logic_vector (3 downto 0);
	
BEGIN	

	adder1: Adder port map(
		A => flipflopsCurrentState,
		B => step,
		C0 => '0',
		S => addRes
	);
	
	muxPL: MUX2_1L4 port map(
		A => addRes,
		B => initial,
		S => PL,
		Y => flipflopsNextState
	);
	
	registry: RegistryL4 port map(
		clk_in => clk_in,
		CLEAR  => CLEAR,
		D => flipflopsNextState,
		CE => CE,
		Q => flipflopsCurrentState
	);
	
	Q <= flipflopsCurrentState;
	Z <= (flipflopsCurrentState(0) AND NOT flipflopsCurrentState(1) AND NOT flipflopsCurrentState(2) AND NOT flipflopsCurrentState(3));
	
	
END Structural;