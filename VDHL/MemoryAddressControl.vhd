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

	
	component Counter
		PORT(	
			clk_in, CE, CLEAR, PL: IN std_logic;
			initial, step: IN std_logic_vector (3 downto 0);
			Q: OUT std_logic_vector (3 downto 0)
		);
	end component;
	
	component MUX2_1L7
		PORT(
			A, B: IN std_logic_vector(6 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector(6 downto 0)
		);
	end component;
	
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
	
	component Adder
		PORT(	
			A,B: IN std_logic_vector (3 downto 0);
			C0: IN std_logic;
			S: OUT std_logic_vector (3 downto 0);
			C4: OUT std_logic
		);
	end component;
	
	signal putIndex, getIndex, invertedPutIndex, subtractorRes: std_logic_vector(3 downto 0);
	signal getFlag, lastActionFlag, pointersEqual, latchReset: std_logic;
	signal muxOut, muxA, muxB: std_logic_vector(6 downto 0);
	
BEGIN

	latchReset <= incGet OR CLEAR;

	SRlatch: FFD port map(
		CLK		=> '0',
		EN 		=> '1', 
		RESET		=> latchReset	, 
		SET		=> incPut, 
		D			=> '0',
		Q			=> lastActionFlag
	);
	
	contPut: Counter port map(
		clk_in 	=> clk_in, 
		CE 		=> incPut, 
		CLEAR		=> CLEAR, 
		PL			=> '0', 
		initial	=> "0000",
		step		=> "0001",
		Q			=> putIndex
	);
		
	contGet: Counter port map(
		clk_in 	=> clk_in, 
		CE 		=> incGet, 
		CLEAR		=> CLEAR, 
		PL			=> '0', 
		initial	=> "0000",
		step		=> "0001",
		Q			=> getIndex
	);
	
	muxA <= "000" & getIndex;
	muxB <= "000" & putIndex;
	
	muxPL: MUX2_1L7 port map(
		A			=> muxA,
		B			=> muxB,
		S			=> putGet,
		Y			=> muxOut
	);
	
	Q <= muxOut(3 downto 0);
	
	invertedPutIndex <= NOT putIndex(3) & NOT putIndex(2) & NOT putIndex(1) & NOT putIndex(0);
	
	adder1: Adder port map(
		A => invertedPutIndex,
		B => getIndex,
		C0 => '1',
		S => subtractorRes
	);
	
	pointersEqual	<= NOT subtractorRes(3) AND NOT subtractorRes(2) 
							AND NOT subtractorRes(1) AND NOT subtractorRes(0);
	
	full <= pointersEqual AND lastActionFlag;
	
	empty <= pointersEqual AND NOT lastActionFlag;
	
	

END Behaviour;