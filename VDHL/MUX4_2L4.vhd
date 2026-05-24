library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX4_2L4 IS
	PORT(
		A, B, C, D: IN std_logic_vector(3 downto 0);
		S: IN std_logic_vector(1 downto 0);
		Y: OUT std_logic_vector(3 downto 0)
	);
END MUX4_2L4;

ARCHITECTURE Behaviour OF MUX4_2L4 IS
	signal s0, s1: std_logic_vector(3 downto 0);

BEGIN
	s0 <= S(0) & S(0) & S(0) & S(0);
	s1 <= S(1) & S(1) & S(1) & S(1);
	
	Y <= (A AND NOT s0 AND NOT s1) OR
			(B AND s0 AND NOT s1) OR
			(C AND NOT s0 AND s1) OR
			(D AND s0 AND s1);
	
	
END Behaviour;