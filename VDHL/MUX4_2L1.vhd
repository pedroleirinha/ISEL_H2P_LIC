library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX4_2L1 IS
	PORT(
		A: IN std_logic_vector(3 downto 0);
		S: IN std_logic_vector(1 downto 0);
		Y: OUT std_logic
	);
END MUX4_2L1;

ARCHITECTURE Behaviour OF MUX4_2L1 IS
BEGIN

	process(S, A)
	begin
	  case S is
			when "00" => Y <= NOT A(0);
			when "01" => Y <= NOT A(1);
			when "10" => Y <= NOT A(2);
			when "11" => Y <= NOT A(3);
	
			when others => Y <= '0';
	  end case;
	end process;
	
END Behaviour;