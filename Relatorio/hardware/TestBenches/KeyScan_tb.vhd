library IEEE;
use IEEE.std_logic_1164.all;

entity KeyScan_tb is
end entity;

architecture KeyScan_tb_arch of KeyScan_tb is

	component KeyScan PORT(
		clk_in, Kscan: IN std_logic;
		rows: 			IN std_logic_vector(3 downto 0);
		cols: 			OUT std_logic_vector(3 downto 0);
		K: 				OUT std_logic_vector (3 downto 0);
		Kpress:			OUT std_logic
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal KSCAN_TB, KPRESS_TB: std_logic;
	signal COLS_TB, K_TB, ROWS_TB  : std_logic_vector(3 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;
	UUT: KeyScan port map(	
		clk_in	=> CLK_TB,
		Kscan		=> KSCAN_TB,
		rows	 	=> ROWS_TB,
		cols		=> COLS_TB,
		K			=> K_TB, 
		Kpress	=> KPRESS_TB
	);
	
stimulus: process 
begin
	
	KSCAN_TB <= '0';
	wait for CLK_PERIOD;
	KSCAN_TB <= '1';
	
	-- nenhuma tecla pressionada
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;

	-- Simular tecla (coluna 3, linha 3)
	-- Supondo lógica ativa a '0'

	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1101";            -- linha 3 ativa
	wait for CLK_PERIOD * 2;
	
	KSCAN_TB <= '0';
	wait for CLK_PERIOD;
	KSCAN_TB <= '1';
	
	-- Simular tecla (coluna 1, linha 4)
	wait until COLS_TB = "0111";  -- coluna 1 ativa
	ROWS_TB <= "1110";           -- linha 4 ativa
	wait for CLK_PERIOD * 2;
	
	KSCAN_TB <= '0';
	wait for CLK_PERIOD;
	KSCAN_TB <= '1';
	
	-- Simular tecla (coluna 4, linha 2)
	wait until COLS_TB = "1110";  -- coluna 4 ativa
	ROWS_TB <= "1011";            -- linha 2 ativa
	wait for CLK_PERIOD * 2;	 
        
	-- libertar tecla
   ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;
		  
	wait;
		
		
end process;

end architecture;