library ieee;
use ieee.std_logic_1164.all;

ENTITY Adder IS
	PORT(
		A,B: IN std_logic_vector (3 downto 0);
		C0: IN std_logic;
		S: OUT std_logic_vector (3 downto 0);
		C4: OUT std_logic
	);
END Adder;

ARCHITECTURE Structural OF Adder IS
	component FullAdder 
		PORT(
			A,B,CBi: IN std_logic;
			S, CO: OUT std_logic
		);
	end component;
	
	signal carries: std_logic_vector(3 downto 0);
	
BEGIN

	adder1: FullAdder port map(
		A => A(0),
		B => B(0),
		CBi => C0,
		S => S(0),
		CO => carries(0)
	);
	
	adder2: FullAdder port map(
		A => A(1),
		B => B(1),
		CBi => carries(0),
		S => S(1),
		CO => carries(1)
	);
	
	adder3: FullAdder port map(
		A => A(2),
		B => B(2),
		CBi => carries(1),
		S => S(2),
		CO => carries(2)
	);
	
	adder4: FullAdder port map(
		A => A(3),
		B => B(3),
		CBi => carries(2),
		S => S(3),
		CO => carries(3)
	);
	
	C4 <= carries(3);

	
END Structural;