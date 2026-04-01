library IEEE;
use IEEE.std_logic_1164.all;

entity SerialReceiver_tb is
end entity;

architecture SerialReceiver_tb_arch of SerialReceiver_tb is

	component SerialReceiver PORT(
		SCLK, SDX, SS, CLEAR: 	IN std_logic;
		Q: 							OUT std_logic_vector(9 downto 0)
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal SDX_TB, SS_TB, CLEAR_TB: std_logic;
	signal Q_TB: std_logic_vector(9 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: SerialReceiver port map(	
		CLEAR	=> CLEAR_TB,
		SCLK	=> CLK_TB,
		SDX	=> SDX_TB,
		SS	 	=> SS_TB,
		Q 		=> Q_TB
	);
	
	 
	
stimulus: process 

	-- Trama de exemplo: RS=1, Dado=0x41 ('A'), E=1 -> "1010000011" (binário lido do fim para o início)
	variable test_data : std_logic_vector(9 downto 0) := "1" & "01000001" & "1"; 
	
begin
	SS_TB		<= '1';
	CLEAR_TB	<= '1';
	wait for CLK_PERIOD * 2;
	CLEAR_TB	<= '0';
	SDX_TB 	<= '0';
	SS_TB		<= '0';
	wait for CLK_PERIOD;
				
	for i in 0 to 9 loop
		SDX_TB <= test_data(i);
      wait for CLK_PERIOD; -- Aguarda um ciclo completo
	end loop;
	
	SS_TB 	<= '1'; --Concluiu o envio
	
	wait for CLK_PERIOD;
	
	SS_TB		<= '0'; --Reinicia o envio
	
	wait for CLK_PERIOD;
				
	for i in 0 to 9 loop
		SDX_TB <= test_data(i);
      wait for CLK_PERIOD; -- Aguarda um ciclo completo
	end loop;
	
	SS_TB <= '1';
	wait for CLK_PERIOD;
end process;

end architecture;