library IEEE;
use IEEE.std_logic_1164.all;

entity KeyTransmitterControl_tb is
end entity;

architecture KeyTransmitterControl_tb_arch of KeyTransmitterControl_tb is

	component KeyTransmitterControl 	PORT(
		clk_in, Load, CLEAR, CE, zeros: IN std_logic;
		kbFree, shiftEnable, PL: OUT std_logic
	);
	
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal CE_TB, LOAD_TB, ZEROS_TB, KBFREE_TB, SHIFT_ENABLE_TB, PL_TB, CLEAR_TB: std_logic;

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: KeyTransmitterControl port map(
		clk_in 		=> CLK_TB,
		CE 			=> CE_TB,
		Load 			=> LOAD_TB,
		zeros			=> ZEROS_TB,
		CLEAR			=> CLEAR_TB,
		kbFree 		=> KBFREE_TB,
		shiftEnable	=> SHIFT_ENABLE_TB,
		PL				=> PL_TB
	);

	
	
stimulus: process 
begin
	CLEAR_TB 	<= '1';
	ZEROS_TB 	<= '0';
	CE_TB 		<= '0';
	LOAD_TB		<= '0';
	wait for CLK_PERIOD;
	CLEAR_TB		<= '0';
	CE_TB 		<= '1';
	
	wait for CLK_PERIOD * 2;
	LOAD_TB		<= '1';
	wait for CLK_PERIOD * 2;
	LOAD_TB		<= '0';
	
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '1';
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '0';
	
	
	wait for CLK_PERIOD * 2;
	LOAD_TB		<= '1';	
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '1';
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '0';
	wait for CLK_PERIOD * 2;
	LOAD_TB		<= '0';	
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '1';
	wait for CLK_PERIOD * 2;
	ZEROS_TB		<= '0';
	
	wait;
		
		
end process;

end architecture;