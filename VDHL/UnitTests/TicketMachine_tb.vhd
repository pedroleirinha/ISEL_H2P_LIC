library IEEE;
use IEEE.std_logic_1164.all;

entity TicketMachine_tb is
end entity;

architecture TicketMachine_tb_arch of TicketMachine_tb is

	component TicketMachine 		PORT(
		CLK, CLEAR:					IN std_logic;
		KEYPAD_LIN: 							IN std_logic_vector(3 downto 0);
		LCD_DATA:		 						OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, Kval:				OUT std_logic;
		KEYPAD_COL: 							OUT std_logic_vector(3 downto 0);
		K: 										OUT std_logic_vector (3 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: OUT STD_LOGIC_VECTOR(7 downto 0) 
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal LCD_RS_TB, LCD_EN_TB, CLEAR_TB, KVAL_TB, KACK_TB: std_logic;
	signal COLS_TB, K_TB, ROWS_TB  : std_logic_vector(3 downto 0);
	signal LCD_DATA_TB  : std_logic_vector(7 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: TicketMachine port map(	
		CLK	=> CLK_TB,
		KEYPAD_LIN	 	=> ROWS_TB,
		CLEAR		=> CLEAR_TB,
		KEYPAD_COL		=> COLS_TB,
		K			=> K_TB, 
		LCD_DATA	=> LCD_DATA_TB,
		LCD_EN	=> LCD_RS_TB,
		LCD_RS	=> LCD_RS_TB
	);
	
stimulus: process 
begin
	CLEAR_TB <= '1';
	ROWS_TB <= "1111";        
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	wait for CLK_PERIOD;
	
	-- Simular tecla (coluna 3, linha 3)
	-- Supondo lógica ativa a '0'

	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1101";            -- linha 3 ativa
	wait for CLK_PERIOD * 10;
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 5;
	
	
	wait;
		
		
end process;

end architecture;