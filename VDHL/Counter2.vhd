library ieee;
use ieee.std_logic_1164.all;

ENTITY Counter2 IS
	PORT(
		clk_in, CE, CLEAR, PL: IN std_logic;
		initial, step: IN std_logic_vector (3 downto 0);
		Q: OUT std_logic_vector (3 downto 0);
		Z: OUT std_logic
	);
END Counter2;

ARCHITECTURE Structural OF Counter2 IS
	component RegistryL2 
		PORT(	
			D: IN std_logic_vector (1 downto 0);
			clk_in, CE, CLEAR: IN std_logic;
			Q: OUT std_logic_vector (1 downto 0)
		);
	end component;
	
	component Adder2
		PORT(	
			A,B: IN std_logic_vector (1 downto 0);
			C0: IN std_logic;
			S: OUT std_logic_vector (1 downto 0);
			C4: OUT std_logic
		);
	end component;
	
	component MUX2
		PORT(
			A,B: IN std_logic_vector (1 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (1 downto 0)
		);
	end component;
	
	signal flipflopsCurrentState: std_logic_vector (1 downto 0);
	signal flipflopsNextState: std_logic_vector (1 downto 0);
	signal addRes: std_logic_vector (1 downto 0);
	
BEGIN	

	adder1: Adder2 port map(
		A => flipflopsCurrentState,
		B => step,
		C0 => '0',
		S => addRes
	);
	
	muxPL: MUX2 port map(
		A => addRes,
		B => initial,
		S => PL,
		Y => flipflopsNextState
	);
	
	registry: RegistryL2 port map(
		clk_in => clk_in,
		CLEAR  => CLEAR,
		D => flipflopsNextState,
		CE => CE,
		Q => flipflopsCurrentState
	);
	
	Q <= flipflopsCurrentState;
	Z <= (flipflopsCurrentState(0) AND NOT flipflopsCurrentState(1));
	
	
END Structural;