library IEEE;
use IEEE.std_logic_1164.all;

entity RAM_tb is
end entity;

architecture RAM_tb_arch of RAM_tb is

	component RAM is
		generic(
			ADDRESS_WIDTH : natural := 4;
			DATA_WIDTH : natural := 4
		);
		port(
			address : in std_logic_vector(ADDRESS_WIDTH - 1 downto 0);
			wr: in std_logic;
			din: in std_logic_vector(DATA_WIDTH - 1 downto 0);
			dout: out std_logic_vector(DATA_WIDTH - 1 downto 0)
		);
	end component;

	signal ADDRESS_TB, DIN_TB, DOUT_TB : std_logic_vector(3 downto 0);
   signal WR_TB      : std_logic;
	 
	-- UUT signals
	constant MCLK_PERIOD : time := 20 ns;

begin

	UUT: RAM 
   generic map (ADDRESS_WIDTH => 4, DATA_WIDTH => 4)
	port map(
		address => ADDRESS_TB,
		wr      => WR_TB,
		din     => DIN_TB,
		dout    => DOUT_TB
	);

	
stimulus: process 
begin
	-- 1. Estado inicial: Leitura de endereço vazio
	ADDRESS_TB <= "0000";
	DIN_TB <= "0000";
	WR_TB <= '0';
	wait for MCLK_PERIOD;

	-- 2. Escrita: Guardar o valor "1010" no endereço 0
	ADDRESS_TB <= "0000";
	DIN_TB     <= "1010";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;

	-- 3. Escrita: Guardar o valor "0101" no endereço 15 (limite da RAM)
	ADDRESS_TB <= "1111";
	DIN_TB     <= "0101";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0';
	wait for MCLK_PERIOD;

  -- 4. Verificação de Leitura: Ler endereço 0
	ADDRESS_TB <= "0000";
	wait for MCLK_PERIOD;
	-- No simulador, verifique se dout_tb apresenta "1010"

	-- 5. Verificação de Leitura: Ler endereço 15
	ADDRESS_TB <= "1111";
	wait for MCLK_PERIOD;
	-- No simulador, verifique se dout_tb apresenta "0101"

	-- 6. Teste de Inibição: Tentar escrever "0000" com wr='0'
	ADDRESS_TB <= "0000";
	DIN_TB     <= "0000";
	WR_TB      <= '0';
	wait for MCLK_PERIOD;
	-- Espera-se que dout_tb mantenha "1010", provando que wr='0' impede a escrita

	
	ADDRESS_TB <= "0000";
	DIN_TB     <= "0000";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;

	
	ADDRESS_TB <= "0001";
	DIN_TB     <= "0001";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "0010";
	DIN_TB     <= "0010";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "0011";
	DIN_TB     <= "0011";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "0100";
	DIN_TB     <= "0100";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "0101";
	DIN_TB     <= "0101";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	ADDRESS_TB <= "0110";
	DIN_TB     <= "0110";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "0111";
	DIN_TB     <= "0111";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1000";
	DIN_TB     <= "1000";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1001";
	DIN_TB     <= "1001";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1010";
	DIN_TB     <= "1010";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	ADDRESS_TB <= "1011";
	DIN_TB     <= "1011";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1100";
	DIN_TB     <= "1100";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1101";
	DIN_TB     <= "1101";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	ADDRESS_TB <= "1110";
	DIN_TB     <= "1110";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
		
	ADDRESS_TB <= "1111";
	DIN_TB     <= "1111";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
		
	ADDRESS_TB <= "1010";
	DIN_TB     <= "1010";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
			
	ADDRESS_TB <= "0000";
	DIN_TB     <= "1010";
	WR_TB      <= '1';
	wait for MCLK_PERIOD;
	WR_TB      <= '0'; -- Finaliza escrita
	wait for MCLK_PERIOD;
	
	
	wait;
		
		
end process;

end architecture;