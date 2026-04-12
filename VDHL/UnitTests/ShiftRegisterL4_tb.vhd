library ieee;
use ieee.std_logic_1164.all;

entity ShiftRegisterL4_tb is
end entity;

architecture behavioral of ShiftRegisterL4_tb is

    -- Componente a testar (UUT)
    component ShiftRegisterL4 IS
        PORT(
            CLK, CE, PL, CLEAR:   IN std_logic;
            D:                    IN std_logic_vector(3 downto 0);
            Q:                    OUT std_logic
        );
    end component;

    -- Sinais internos para ligação à UUT
    signal CLK_TB: std_logic := '0'; 
	 signal CE_TB, PL_TB, CLEAR_TB : std_logic;
    signal D_TB : std_logic_vector(3 downto 0);
    signal Q_TB : std_logic;

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;

begin

	CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
	-- Instanciação da Unit Under Test (UUT) [1]
	UUT: ShiftRegisterL4 port map (
		CLK => CLK_TB,
		CE => CE_TB,
		PL => PL_TB,
		CLEAR => CLEAR_TB,
		D => D_TB,
		Q => Q_TB
	);

stimulus: process
begin
 
	CLEAR_TB <= '0';
	PL_TB <= '0';
	CE_TB <= '0';
 
	wait for CLK_PERIOD;
	-- 1. Reset inicial: Garante que o registo começa a zeros
	CLEAR_TB <= '1';
	wait for CLK_PERIOD;
	CLEAR_TB <= '0';
	CE_TB <= '1';
	wait for CLK_PERIOD;
	
	-- 2. Carga Paralela: Carrega o valor "1011"
	D_TB  <= "1011";
	PL_TB <= '1'; -- Ativa carga paralela
	wait for CLK_PERIOD;
	PL_TB <= '0'; -- Passa para modo de deslocamento
	wait for CLK_PERIOD * 6;
	CE_TB <= '0';
	wait for CLK_PERIOD;
	D_TB  <= "1111";
	wait for CLK_PERIOD * 6;
	
	
	
	-- SEGUNDO TESTE
	
	
	-- 2. Carga Paralela: Carrega o valor "1011"
	D_TB  <= "1101";
	CE_TB <= '1';
	PL_TB <= '1'; -- Ativa carga paralela
	wait for CLK_PERIOD;
	PL_TB <= '0'; -- Passa para modo de deslocamento

	-- 3. Teste de Deslocamento: Observar os bits a sair por Q
	-- Nota: No seu código original Q está ligado a D(3). 
	-- Para um shift register real, verifique se Q não deveria ser currRegState(3).
	wait for CLK_PERIOD * 4;

	  -- 4. Teste de Clock Enable (CE): Desativar CE e mudar dados
	CE_TB <= '0';
	D_TB  <= "0000";
	wait for CLK_PERIOD * 2;
	  -- O estado interno não deve mudar enquanto CE = '0'

	CE_TB <= '1';
	wait for CLK_PERIOD * 2;
	  
	wait; 
 end process;

end architecture;