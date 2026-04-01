library IEEE;
use IEEE.std_logic_1164.all;

entity ShiftRegister_tb is
end entity;

architecture ShiftRegister_tb_arch of ShiftRegister_tb is

	component ShiftRegister PORT(
		SCLK, enableShift, SerialIn, CLEAR: 	IN std_logic;
		Q: 					OUT std_logic_vector(9 downto 0)
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal enableShift_TB, SerialIn_TB, CLEAR_TB: std_logic;
	signal Q_TB: std_logic_vector(9 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: ShiftRegister port map(	
		SCLK			=> CLK_TB,
		enableShift	=> enableShift_TB,
		SerialIn	 	=> SerialIn_TB,
		CLEAR			=> CLEAR_TB,
		Q 				=> Q_TB
	);
	
stimulus: process 
begin
	-- Reset
	CLEAR_TB <= '1';
	enableShift_TB <= '1';
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';

	-- Enable shifting
	enableShift_TB <= '0';

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	SerialIn_TB <= '0';
	wait for CLK_PERIOD;

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	SerialIn_TB <= '0';
	wait for CLK_PERIOD;

	-- Stop shifting
	enableShift_TB <= '1';
	wait for 50 ns;
	
		
	-- Restart

	enableShift_TB <= '0';

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	SerialIn_TB <= '0';
	wait for CLK_PERIOD;

	SerialIn_TB <= '0';
	wait for CLK_PERIOD;

	SerialIn_TB <= '1';
	wait for CLK_PERIOD;

	
	wait;
		
		
end process;

end architecture;