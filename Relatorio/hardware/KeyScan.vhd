library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyScan IS
	PORT(
		clk_in, Kscan, CLEAR: IN std_logic;
		rows: 			IN std_logic_vector(3 downto 0);
		cols: 			OUT std_logic_vector(3 downto 0);
		K: 				OUT std_logic_vector (3 downto 0);
		Kpress:			OUT std_logic
	);
END KeyScan;

ARCHITECTURE Behaviour OF KeyScan IS
	component Counter
		PORT(	
			clk_in, CE, CLEAR, PL: IN std_logic;
			initial, step: IN std_logic_vector (3 downto 0);
			Q: OUT std_logic_vector (3 downto 0);
			Z: OUT std_logic
		);
	end component;
	
	component decoder 
		PORT(	
			A: OUT std_logic_vector (3 downto 0);
			S: IN std_logic_vector (1 downto 0)
		);
	end component;
	
	component MUX4_2L1
		PORT(
			A: IN std_logic_vector(3 downto 0);
			S: IN std_logic_vector(1 downto 0);
			Y: OUT std_logic
		);
	end component;
	
	signal countValues: std_logic_vector (3 downto 0);
	signal zeros: std_logic;
	signal decodeOutputs: std_logic_vector (3 downto 0);
	

BEGIN

	cont: Counter port map(
		clk_in 	=> clk_in, 
		CE 		=> Kscan, 
		CLEAR		=> CLEAR, 
		PL			=> '0', 
		initial	=> "0000",
		step		=> "0001",
		Q			=> countValues,
		Z			=> zeros
	);
	
	muxPL: MUX4_2L1 port map(
		A			=> rows,
		S			=> countValues(1 downto 0),
		Y			=> Kpress

	);
	
	decode: decoder port map(
		A 			=> decodeOutputs,
		S 			=> countValues(3 downto 2)
	);
	
	cols <= decodeOutputs;
	K <= countValues;

END Behaviour;