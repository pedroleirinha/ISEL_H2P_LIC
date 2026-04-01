library IEEE;
use IEEE.std_logic_1164.all;

entity KeyDecode_tb is
end entity;

architecture KeyDecode_tb_arch of KeyDecode_tb is

	component KeyDecode 	PORT(
		clk_in, Kack, CLEAR: 	IN std_logic;
		rows: 						IN std_logic_vector(3 downto 0);
		cols: 						OUT std_logic_vector(3 downto 0);
		K: 							OUT std_logic_vector (3 downto 0);
		Kval:							OUT std_logic
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal KACK_TB, KPRESS_TB, KVAL_TB, CLEAR_TB: std_logic;
	signal COLS_TB, K_TB, ROWS_TB  : std_logic_vector(3 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: KeyDecode port map(	
		clk_in	=> CLK_TB,
		Kack		=> KACK_TB,
		rows	 	=> ROWS_TB,
		CLEAR		=> CLEAR_TB,
		cols		=> COLS_TB,
		K			=> K_TB, 
		Kval	=> KVAL_TB
	);
	
stimulus: process 
begin

	CLEAR_TB <= '1';
	KACK_TB 	<= '0';
	ROWS_TB <= "1111";        
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	wait for CLK_PERIOD;
	
	-- Simular tecla (coluna 3, linha 3)
	-- Supondo lógica ativa a '0'

	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1101";            -- linha 3 ativa
	wait for CLK_PERIOD * 5;
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;
	KACK_TB 	<= '1';
	wait for CLK_PERIOD * 5;
	KACK_TB 	<= '0';
	wait for CLK_PERIOD;
		
	
	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1110";            -- linha 3 ativa
	wait for CLK_PERIOD * 5;
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;
	KACK_TB 	<= '1';
	wait for CLK_PERIOD * 5;
	
	KACK_TB 	<= '0';
	wait for CLK_PERIOD;
	
	
	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "0111";            -- linha 3 ativa
	wait for CLK_PERIOD * 5;
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;
	KACK_TB 	<= '1';
	wait for CLK_PERIOD * 5;
	
	KACK_TB 	<= '0';
	wait for CLK_PERIOD;
	CLEAR_TB <= '1';

	wait;
		
		
end process;

end architecture;