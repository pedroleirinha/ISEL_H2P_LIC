library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;


ENTITY RandomAccessMemory IS
    PORT (
        clk_in, Wr:		in  std_logic;                         
        Addr, dataIn: 	in  std_logic_vector(3 downto 0);      
        dataOut:			out std_logic_vector(3 downto 0)      
    );
END RandomAccessMemory;

ARCHITECTURE Behaviour OF RandomAccessMemory IS
	 -- Definição do tipo para a memória: 16 posições de 4 bits [2]
    type memory_t is array (0 to 15) of std_logic_vector(3 downto 0);
    signal ram_block : memory_t;
	 
BEGIN
	 -- Processo de Escrita Síncrona
    process(clk_in)
    begin
        if rising_edge(clk_in) then
            if Wr = '1' then
                ram_block(to_integer(unsigned(Addr))) <= dataIn;
            end if;
        end if;
    end process;

    -- Leitura Combinatória (Assíncrona)
    -- O valor à saída atualiza mal o endereço addr mude
    dataOut <= ram_block(to_integer(unsigned(Addr)));
	

END Behaviour;