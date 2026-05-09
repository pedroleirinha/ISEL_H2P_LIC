library IEEE;
use IEEE.std_logic_1164.all;

entity RingBuffer_tb is
end entity;

architecture RingBuffer_tb_arch of RingBuffer_tb is

	component RingBuffer 	PORT(
		clk_in, DAV, CTS, CLEAR: 	IN std_logic;
		D: 								IN std_logic_vector(3 downto 0);
		Q: 								OUT std_logic_vector (3 downto 0);
		Wreg, DAC:						OUT std_logic
	);
	
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal DAV_TB, CTS_TB, WREG_TB, DAC_TB, CLEAR_TB: std_logic;
	signal D_TB, Q_TB: std_logic_vector(3 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: RingBuffer port map(
		clk_in	=> CLK_TB,
		DAV		=> DAV_TB,
		CTS		=>	CTS_TB,
		Wreg		=>	WREG_TB,
		D			=> D_TB,
		Q			=> Q_TB,
		DAC		=>	DAC_TB,
		CLEAR		=>	CLEAR_TB
	);

	
stimulus: process 
begin
	CLEAR_TB 	<= '1';
	DAV_TB 		<= '0';
	CTS_TB 		<= '0';
	wait for CLK_PERIOD;
	CLEAR_TB 	<= '0';
	
	
	D_TB			<= "0110";
	-- Write Cycle
	wait for CLK_PERIOD;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 5;
	
		
	D_TB			<= "1111";
	-- Write Cycle
	wait for CLK_PERIOD;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 5;
	
		
	D_TB			<= "0010";
	-- Write Cycle
	wait for CLK_PERIOD;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 5;
	
		
	D_TB			<= "0000";
	-- Write Cycle
	wait for CLK_PERIOD;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 5;
	
	-- Read Cycle	
	CTS_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	CTS_TB 		<= '0';
	wait for CLK_PERIOD * 5;	
	
		-- Read Cycle	
	CTS_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	CTS_TB 		<= '0';
	wait for CLK_PERIOD * 5;	
	
		-- Read Cycle	
	CTS_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	CTS_TB 		<= '0';
	wait for CLK_PERIOD * 5;	
	
		-- Read Cycle	
	CTS_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	CTS_TB 		<= '0';
	wait for CLK_PERIOD * 5;	
	

	
	wait;
		
		
end process;

end architecture;