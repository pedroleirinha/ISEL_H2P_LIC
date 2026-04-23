library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX2_1L7 IS
	PORT(
		A,B: IN std_logic_vector (6 downto 0);
		S: IN std_logic;
		Y: OUT std_logic_vector (6 downto 0)
	);
END MUX2_1L7;

ARCHITECTURE Behaviour OF MUX2_1L7 IS
BEGIN
	Y(0) <= (A(0) AND NOT S) OR (B(0) AND S);
	Y(1) <= (A(1) AND NOT S) OR (B(1) AND S);
	Y(2) <= (A(2) AND NOT S) OR (B(2) AND S);
	Y(3) <= (A(3) AND NOT S) OR (B(3) AND S);
	Y(4) <= (A(4) AND NOT S) OR (B(4) AND S);
	Y(5) <= (A(5) AND NOT S) OR (B(5) AND S);
	Y(6) <= (A(6) AND NOT S) OR (B(6) AND S);
	
END Behaviour;