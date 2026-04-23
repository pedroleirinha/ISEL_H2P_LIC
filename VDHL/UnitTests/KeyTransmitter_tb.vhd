library IEEE;
use IEEE.std_logic_1164.all;

entity KeyTransmitter_tb is
end entity;

architecture KeyTransmitter_tb_arch of KeyTransmitter_tb is

	component KeyTransmitter PORT(
		CLK ,TxClk, Load, CLEAR:IN std_logic;
		D:		 						IN std_logic_vector(3 downto 0);
		TxD, KbFree:				OUT std_logic
	);
	end component;

	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal TXCLK_TB, LOAD_TB, TXD_TB, CLEAR_TB, KBFREE_TB: std_logic;
	signal BUFFER_TB: std_logic_vector(3 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	UUT: KeyTransmitter port map(	
		CLK		=> CLK_TB,
		TxClk 	=> TXCLK_TB,
		Load 		=> LOAD_TB,
		CLEAR 	=> CLEAR_TB,
		D 			=> BUFFER_TB,		
		TxD 		=> TXD_TB,	
		KbFree 	=> KBFREE_TB
	);
	
stimulus: process 
	
	variable test_data : std_logic_vector(3 downto 0) := "0110"; 

begin
	TXCLK_TB	<= '0';
	CLEAR_TB <= '1';
	LOAD_TB	<= '0';
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	wait for CLK_PERIOD	;
	BUFFER_TB<= "0110";
	wait for CLK_PERIOD;
	LOAD_TB	<= '1';
	wait for CLK_PERIOD;
	LOAD_TB	<= '0';
	wait for CLK_PERIOD;

	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '1';
	wait for CLK_PERIOD; 
	TXCLK_TB	<= '0';
	wait;
	
end process;

end architecture;