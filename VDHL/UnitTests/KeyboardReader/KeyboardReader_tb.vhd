library IEEE;
use IEEE.std_logic_1164.all;

entity KeyboardReader_tb is
end entity;

architecture KeyboardReader_tb_arch of KeyboardReader_tb is

	component KeyboardReader 	PORT(
		clk_in, CLEAR, TxClk: 			IN std_logic;
		rows: 								IN std_logic_vector(3 downto 0);
		delay:					         IN std_logic_vector(1 downto 0); 
		cols: 								OUT std_logic_vector(3 downto 0);
		K: 									OUT std_logic_vector (3 downto 0);
		Kval, TxD, KbFree:				OUT std_logic
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal TXCLK_TB, TXD_TB, CLEAR_TB, KBFREE_TB, KVAL_TB: std_logic;
	signal COLS_TB, K_TB, ROWS_TB  : std_logic_vector(3 downto 0);
	signal DELAY_TB: std_logic_vector(1 downto 0);


	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: KeyboardReader port map(	
		clk_in	=> CLK_TB,
		TxClk 	=> TXCLK_TB,
		CLEAR 	=> CLEAR_TB,
		rows	 	=> ROWS_TB,
		cols		=> COLS_TB,
		delay		=> DELAY_TB,
		K 			=> K_TB,		
		TxD 		=> TXD_TB,	
		Kval 		=> KVAL_TB,	
		KbFree 	=> KBFREE_TB
	);
	
stimulus: process 

begin
	DELAY_TB <= "00";
	CLEAR_TB <= '1';
	ROWS_TB <= "1111";        
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	wait for CLK_PERIOD;
	
	-- Simular tecla (coluna 3, linha 3)
	-- Supondo lógica ativa a '0'

	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1101";            -- linha 3 ativa
	wait for CLK_PERIOD * 5;
	ROWS_TB <= "1111";
	
	wait for CLK_PERIOD * 6;
	
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo
	end loop;
	
	
	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1110";            -- linha 3 ativa
	wait for CLK_PERIOD * 9;
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;

	
	DELAY_TB <= "01";
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;
	
	DELAY_TB <= "10";
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;
	
	DELAY_TB <= "11";
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;
	
	
	DELAY_TB <= "00";
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;
	
	wait until TXD_TB = '0';
	for i in 0 to 8 loop
		TXCLK_TB	<= '1';
		wait for CLK_PERIOD; 
		TXCLK_TB	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo 
	end loop;
	
	wait;
	
end process;

end architecture;