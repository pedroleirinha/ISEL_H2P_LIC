library IEEE;
use IEEE.std_logic_1164.all;

entity KeyControl_tb is
end entity;

architecture KeyControl_tb_arch of KeyControl_tb is

	component KeyControl PORT(
		clk_in, Kpress, Kack, CLEAR, CE: IN std_logic;
		Kval, Kscan: OUT std_logic
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal KSCAN_TB, KPRESS_TB, KACK_TB, KVAL_TV, CLEAR_TB: std_logic;

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: KeyControl port map(
		clk_in	=> CLK_TB,
		Kpress	=> KPRESS_TB,
		Kack		=> KACK_TB,
		CLEAR		=> CLEAR_TB,
		CE			=> '1',
		Kval		=> KVAL_TV,
		Kscan		=> KSCAN_TB
	);

	
stimulus: process 
begin
	CLEAR_TB 	<= '1';
	KPRESS_TB 	<= '0';
	KACK_TB 		<= '0';
	wait for CLK_PERIOD;	
	wait;
		
		
end process;

end architecture;