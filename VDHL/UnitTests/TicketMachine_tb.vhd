library IEEE;
use IEEE.std_logic_1164.all;

entity TicketMachine_tb is
end entity;

architecture TicketMachine_tb_arch of TicketMachine_tb is

	component TicketMachine 				PORT(
		CLK, CLEAR, CollectTicket, Coin:		IN std_logic;
		KEYPAD_LIN:									IN std_logic_vector(3 downto 0);
      delay:					               IN std_logic_vector(1 downto 0); 
		COINS: 										IN std_logic_vector(2 downto 0);
		output:										IN std_logic_vector(7 downto 0);
		LCD_DATA:		 							OUT std_logic_vector(7 downto 0);
		LCD_EN, LCD_RS, CoinAccepted, Prt:	OUT std_logic; 
		KEYPAD_COL: 								OUT std_logic_vector(3 downto 0);
		K: 											OUT std_logic_vector(3 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: OUT STD_LOGIC_VECTOR(7 downto 0);
		state:										OUT std_logic_vector(7 downto 0)
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal COINS_TB: std_logic_vector(2 downto 0);
	signal LCD_RS_TB, LCD_EN_TB, CLEAR_TB, COLLECT_TICKET_TB, COIN_TB, COIN_ACCEPT_TB, PRT_TB: std_logic;
	signal HEX0_TB, HEX1_TB, HEX2_TB, HEX3_TB, HEX4_TB, HEX5_TB: STD_LOGIC_VECTOR(7 downto 0);

	signal STATE_TB: std_logic_vector(7 downto 0);
	signal DELAY_TB: std_logic_vector(1 downto 0);
	signal COLS_TB, K_TB, ROWS_TB  : std_logic_vector(3 downto 0);
	signal LCD_DATA_TB, OUTPUT_TB  : std_logic_vector(7 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: TicketMachine port map(	
		CLK				=> CLK_TB,
		KEYPAD_LIN	 	=> ROWS_TB,
		CLEAR				=> CLEAR_TB,
		KEYPAD_COL		=> COLS_TB,
		K					=> K_TB, 
		delay				=> DELAY_TB,
		LCD_DATA			=> LCD_DATA_TB,
		LCD_EN			=> LCD_EN_TB,
		LCD_RS			=> LCD_RS_TB,
		CollectTicket	=> COLLECT_TICKET_TB,
		Coin				=> COIN_TB,
		COINS				=> COINS_TB,
		output			=> OUTPUT_TB,
		state				=> STATE_TB,
		coinAccepted	=> COIN_ACCEPT_TB,
		Prt				=> PRT_TB,
		HEX0				=> HEX0_TB,
		HEX1				=> HEX1_TB,
		HEX2				=> HEX2_TB,
		HEX3				=> HEX3_TB,
		HEX4				=> HEX4_TB,
		HEX5				=> HEX5_TB
	);
	
stimulus: process 
	variable test_data : std_logic_vector(9 downto 0) := "1" & "01000001" & "1"; 
	variable test_data2 : std_logic_vector(9 downto 0) := "0" & "01000001" & "1"; 
begin
	COLLECT_TICKET_TB	<= '0';
	DELAY_TB	<= "00";
	PRT_TB	<= '0';
	COINS_TB <= "000";
	OUTPUT_TB<= "00001000";
	CLEAR_TB <= '1';
	COIN_TB	<= '0';
	ROWS_TB 	<= "1111";        
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	wait for CLK_PERIOD;
	
	-- Simular tecla (coluna 3, linha 3)
	-- Supondo lógica ativa a '0'

	wait until COLS_TB = "1101";  -- coluna 3 ativa
	ROWS_TB <= "1101";            -- linha 3 ativa
	wait for CLK_PERIOD * 5;
	ROWS_TB <= "1111";
	wait for CLK_PERIOD * 7;
	
	-- Começa o TxCLK
	for i in 0 to 9 loop
		OUTPUT_TB(7)	<= '1';
		wait for CLK_PERIOD; 
		OUTPUT_TB(7)	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo
	end loop;
	-- FIM DO TXCLK
	
	wait for CLK_PERIOD;
	COINS_TB <= "010";
	COIN_TB <= '1';
	OUTPUT_TB(4) <= '1';
	wait for CLK_PERIOD * 3;
	COIN_TB <= '0';
	OUTPUT_TB(4) <= '0';
	wait for CLK_PERIOD;
	COINS_TB <= "111";
	COIN_TB <= '1';
	OUTPUT_TB(4) <= '1';
	wait for CLK_PERIOD * 3;
	COIN_TB <= '0';
	OUTPUT_TB(4) <= '0';
	wait for CLK_PERIOD;
	
	OUTPUT_TB(3) <= '0';
	
	for i in 0 to 9 loop

		 OUTPUT_TB(0) <= test_data(i);
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '1';
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '0';
		 wait for CLK_PERIOD;

	end loop;
	
	OUTPUT_TB(3) <= '1';
	
	wait for CLK_PERIOD ;	
	PRT_TB <= '1';
	wait for CLK_PERIOD ;	
		
	OUTPUT_TB(3) <= '0';
	
	for i in 0 to 9 loop

		 OUTPUT_TB(0) <= test_data2(i);
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '1';
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '0';
		 wait for CLK_PERIOD;

	end loop;
	
	OUTPUT_TB(3) <= '1';
	PRT_TB <= '0';
	
	
	-- Segundo processo
	
	
		wait until COLS_TB = "0111";  -- coluna 3 ativa
	ROWS_TB <= "0111";            -- linha 3 ativa
	wait for CLK_PERIOD * 7;
	
	-- Começa o TxCLK
	for i in 0 to 9 loop
		OUTPUT_TB(7)	<= '1';
		wait for CLK_PERIOD; 
		OUTPUT_TB(7)	<= '0';
      wait for CLK_PERIOD; -- Aguarda um ciclo completo
	end loop;
	-- FIM DO TXCLK
	
	wait for CLK_PERIOD;
	COINS_TB <= "011";
	COIN_TB <= '1';
	OUTPUT_TB(4) <= '1';
	wait for CLK_PERIOD * 3;
	COIN_TB <= '0';
	OUTPUT_TB(4) <= '0';
	wait for CLK_PERIOD;
	COINS_TB <= "111";
	COIN_TB <= '1';
	OUTPUT_TB(4) <= '1';
	wait for CLK_PERIOD * 3;
	COIN_TB <= '0';
	OUTPUT_TB(4) <= '0';
	wait for CLK_PERIOD;
	
	OUTPUT_TB(3) <= '0';
	
	for i in 0 to 9 loop

		 OUTPUT_TB(0) <= test_data(i);
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '1';
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '0';
		 wait for CLK_PERIOD;

	end loop;
	
	OUTPUT_TB(3) <= '1';
	
	wait for CLK_PERIOD ;	
	PRT_TB <= '1';
	wait for CLK_PERIOD ;	
		
	OUTPUT_TB(3) <= '0';
	
	for i in 0 to 9 loop

		 OUTPUT_TB(0) <= test_data2(i);
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '1';
		 wait for CLK_PERIOD/2;

		 OUTPUT_TB(1) <= '0';
		 wait for CLK_PERIOD;

	end loop;
	
	OUTPUT_TB(3) <= '1';
	PRT_TB <= '0';
	

	wait;
		
		
end process;

end architecture;