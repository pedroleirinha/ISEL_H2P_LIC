library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

ENTITY CLKDIV_timer IS
    generic( div: natural := 25000000 ); -- Valor padrão para 500ms a 50MHz
    port ( 
        clk_in  : in  std_logic;
        reset   : in  std_logic; 
        clk_out : out std_logic
    );
END CLKDIV_timer;

ARCHITECTURE bhv OF CLKDIV_timer IS
    signal count: integer := 1;
    signal tmp  : std_logic := '0';
BEGIN

process(clk_in, reset)
begin
    if (reset = '1') then
        count <= 1;
        tmp   <= '0';
    elsif (clk_in'event and clk_in='1') then
        count <= count + 1;
        
        if (count = div/2) then
            tmp   <= NOT tmp;
            count <= 1;
        end if;
    end if;
end process;


clk_out <= tmp;

End bhv;
