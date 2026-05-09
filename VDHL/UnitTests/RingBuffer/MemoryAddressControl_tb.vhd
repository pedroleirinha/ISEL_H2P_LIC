library IEEE;
use IEEE.std_logic_1164.all;

entity MemoryAddressControl_tb is
end entity;

architecture MemoryAddressControl_tb_arch of MemoryAddressControl_tb is

	component MemoryAddressControl 	PORT(
			clk_in, putGet, CLEAR, incPut, incGet: IN std_logic;
			Q:		 											OUT std_logic_vector(3 downto 0);
			full, empty:									OUT std_logic
		);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal PUTGET_TB, INCGET_TB, FULL_TB, EMPTY_TB, INCPUT_TB, CLEAR_TB: std_logic;
	signal Q_TB: std_logic_vector(3 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: MemoryAddressControl port map(
		clk_in	=> CLK_TB,
		putGet	=> PUTGET_TB,
		CLEAR		=>	CLEAR_TB,
		full		=>	FULL_TB,
		empty		=>	EMPTY_TB,
		incPut	=>	INCPUT_TB,
		incGet	=>	INCGET_TB,
		Q			=>	Q_TB
	);

	
stimulus: process 
begin
	CLEAR_TB 	<= '1';
	PUTGET_TB 	<= '0';
	INCPUT_TB 	<= '0';
	INCGET_TB 	<= '0';
	
	wait for CLK_PERIOD;
	CLEAR_TB 	<= '0';
	wait for CLK_PERIOD;
	
	-- Estamos a tentar ler da RAM
	-- Incrementa o endereço do GET
	INCGET_TB 	<= '1';
	wait for CLK_PERIOD;
	INCGET_TB 	<= '1';
	wait for CLK_PERIOD;
	INCGET_TB 	<= '1';
	wait for CLK_PERIOD;
	INCGET_TB 	<= '1';
	wait for CLK_PERIOD;
	INCGET_TB 	<= '0';
	wait for CLK_PERIOD * 4;
	
	-- Estamos a tentar escrever da RAM
	-- Incrementa o endereço do PUT
	PUTGET_TB 	<= '1';	
	INCPUT_TB 	<= '1';
	wait for CLK_PERIOD;
	INCPUT_TB 	<= '1';
	wait for CLK_PERIOD;
	INCPUT_TB 	<= '1';
	wait for CLK_PERIOD;
	INCPUT_TB 	<= '1';
	wait for CLK_PERIOD;
	INCPUT_TB 	<= '1';
	wait for CLK_PERIOD;
	INCPUT_TB 	<= '0';
	wait for CLK_PERIOD * 4;
	wait;
		
		
end process;

end architecture;