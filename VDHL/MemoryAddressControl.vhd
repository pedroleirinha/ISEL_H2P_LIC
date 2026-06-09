library ieee;
use ieee.std_logic_1164.all;

ENTITY MemoryAddressControl IS
	PORT(
		clk_in, putGet, CLEAR, incPut, incGet: IN std_logic;
		Q:		 											OUT std_logic_vector(3 downto 0);
		full, empty:									OUT std_logic;
		
		putIndex_out, getIndex_out:				OUT std_logic_vector(3 downto 0)
		
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
	
	signal putIndex, getIndex: std_logic_vector(3 downto 0);
	signal invertedPutIndex, subtractorRes: std_logic_vector(3 downto 0);
	signal getFlag, lastActionFlag, pointersEqual, latchReset: std_logic;
	signal muxOut, muxA, muxB: std_logic_vector(6 downto 0);
	
	
	signal incGet_confirmed, incPut_confirmed: std_logic;
	signal empty_state, full_state: std_logic;
	
BEGIN

	putIndex_out <= putIndex; -- TEMP
	getIndex_out <= getIndex; -- TEMP


-- HS:2026.05.31 - BEGIN	
--	latchReset <= incGet OR CLEAR;
--	
--	SRlatch: FFD port map(
--		CLK		=> '0',
--		EN 		=> '1', 
--		RESET		=> latchReset	, 
--		SET		=> incPut, 
--		D			=> '0',
--		Q			=> lastActionFlag
--	);

--  Garatir consistencia entre modulo impondo comportamento
-- esperado as entradas incGet e incPut.

	empty_STATE <= pointersEqual AND NOT lastActionFlag;
	full_STATE <= pointersEqual AND lastActionFlag;

	
--	Reg_empty: FFD port map(
--		CLK	=> clk_in,
--		EN		=> '1',
--		RESET => CLEAR,
--		SET	=> '0',
--		D		=> pointersEqual AND NOT lastActionFlag,
--		Q		=> empty_STATE
--	);

--	Reg_full: FFD port map(
--		CLK	=> clk_in,
--		EN		=> '1',
--		RESET => CLEAR,
--		SET	=> '0',
--		D		=> pointersEqual AND lastActionFlag,
--		Q		=> full_STATE
--	);
	
	
	incGet_confirmed <= incGet and not empty_STATE;
	incPut_confirmed <= incPut and not full_STATE;
	
	
	

	SRLatch: FFD port map(
		CLK	=> clk_in,
		EN		=> incPut_confirmed xor incGet_confirmed, --poderia ser 'or' para prevenir glitches
		RESET => CLEAR,
		SET	=> '0',
		D		=> incPut_confirmed, -- quando incPut é 0 então é porque incGet é 1, pois impusemos por XOR.
		Q		=> lastActionFlag
	);

	
-- HS:2026.05.31 - END
	
	contPut: Counter port map(
		clk_in 	=> clk_in, 
		CE 		=> incPut_confirmed, 
		CLEAR		=> CLEAR, 
		PL			=> '0', 
		initial	=> "0000",
		step		=> "0001",
		Q			=> putIndex
	);
		
	contGet: Counter port map(
		clk_in 	=> clk_in,
		CE 		=> incGet_confirmed,
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
	
	
	-- full <= pointersEqual AND lastActionFlag; -- descontinuado
	-- empty <= pointersEqual AND NOT lastActionFlag; -- descontinuado
	
	empty <= empty_STATE;
	full <= full_STATE;
	

END Behaviour;