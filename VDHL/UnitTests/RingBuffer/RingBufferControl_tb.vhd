library IEEE;
use IEEE.std_logic_1164.all;

entity RingBufferControl_tb is
end entity;

architecture RingBufferControl_tb_arch of RingBufferControl_tb is

	component RingBufferControl 	PORT(
		clk_in, DAV, CTS, full, empty, CLEAR: 		IN std_logic;
		Wreg, DAC, Wr, selPG, incPut, incGet:		OUT std_logic
	);
	
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal DAV_TB, CTS_TB, FULL_TB, EMPTY_TB, WREG_TB, DAC_TB, WR_TB, SELPG_TB, INCPUT, INCGET, CLEAR_TB: std_logic;

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: RingBufferControl port map(
		clk_in	=> CLK_TB,
		DAV		=> DAV_TB,
		CTS		=>	CTS_TB,
		full		=>	FULL_TB,
		empty		=>	EMPTY_TB,
		Wreg		=>	WREG_TB,
		DAC		=>	DAC_TB,
		Wr			=>	WR_TB,
		selPG		=>	SELPG_TB,
		incPut	=> INCPUT,
		incGet   =>	INCGET,
		CLEAR		=>	CLEAR_TB
	);

	
stimulus: process 
begin
	CLEAR_TB 	<= '1';
	DAV_TB 		<= '0';
	CTS_TB 		<= '0';
	FULL_TB 		<= '0';
	EMPTY_TB		<= '0';
	
	wait for CLK_PERIOD;
	CLEAR_TB 	<= '0';
	-- Write Cycle
	wait for CLK_PERIOD;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 10;
	
	-- Read Cycle	
	CTS_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	CTS_TB 		<= '0';
	wait for CLK_PERIOD * 10;	
	
	--NEW READ
	FULL_TB		<= '1';
	wait for CLK_PERIOD * 5;	
	DAV_TB 		<= '1';
	wait for CLK_PERIOD * 5;
	DAV_TB 		<= '0';
	wait for CLK_PERIOD * 3;	
	FULL_TB		<= '0';
	wait for CLK_PERIOD * 3;	
	
	wait;
		
		
end process;

end architecture;