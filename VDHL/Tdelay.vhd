library ieee;
use ieee.std_logic_1164.all;

ENTITY Tdelay IS
    PORT(
        clk_in : IN  std_logic;                    
        ce     : IN  std_logic;                    
        reset  : IN  std_logic;                    
        delay  : IN  std_logic_vector(1 downto 0); 
        S      : OUT std_logic                     
    );
END Tdelay;

ARCHITECTURE Behaviour OF Tdelay IS

    component MUX4_2L1
        PORT(
            A: IN std_logic_vector(3 downto 0);
            S: IN std_logic_vector(1 downto 0);
            Y: OUT std_logic
        );
    end component; 

    component Counter
        PORT(
            clk_in, CE, CLEAR, PL: IN std_logic;
            initial, step: IN std_logic_vector (3 downto 0);
            Q: OUT std_logic_vector (3 downto 0);
            Z: OUT std_logic
        );
    end component;
    
    -- Chamamos o novo divisor feito à imagem do teu original
    component CLKDIV_timer
        generic( div: natural := 50000 ); 
        port ( 
            clk_in  : in  std_logic;
            reset   : in  std_logic;
            clk_out : out std_logic
        );
    end component;

    signal pulso_500ms   : std_logic;
    signal ce_do_contador: std_logic;
    signal q_contador    : std_logic_vector(3 downto 0);
    signal sinais_mux    : std_logic_vector(3 downto 0); 
    signal mux_out       : std_logic;

BEGIN

    -- Forçamos o valor do 'div' para 25_000_000 (500 ms) através do generic map
    Inst_CLKDIV: CLKDIV_timer port map(
            clk_in  => clk_in,
            reset   => reset, -- Garante que o divisor zera se a tecla for solta!
            clk_out => pulso_500ms
        );

    -- O teu contador só avança quando a FSM permite e o clock div bate o tempo
    ce_do_contador <= ce AND pulso_500ms;

    Inst_Counter: Counter
        port map(
            clk_in  => clk_in,
            CE      => ce_do_contador,
            CLEAR   => reset,        
            PL      => '0',
            initial => "0000",
            step    => "0001",       
            Q       => q_contador,
            Z       => open
        );

    -- Lógica inversa para anular o NOT do teu MUX
    sinais_mux(0) <= '1' when (q_contador >= "0001") else '0'; -- 500 ms
    sinais_mux(1) <= '1' when (q_contador >= "0010") else '0'; -- 1000 ms
    sinais_mux(2) <= '1' when (q_contador >= "0011") else '0'; -- 1500 ms
    sinais_mux(3) <= '1' when (q_contador >= "0100") else '0'; -- 2000 ms
	 
    Inst_MUX: MUX4_2L1
        port map(
            A => sinais_mux, 
            S => delay,      
            Y => mux_out     
        );

    S <= mux_out AND ce;

END Behaviour;
