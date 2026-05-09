library IEEE;
use IEEE.std_logic_1164.all;

entity Decode_tb is
end entity;

architecture Decode_tb_arch of Decode_tb is

	component decoder PORT(
		A: OUT std_logic_vector (3 downto 0);
		S: IN std_logic_vector (1 downto 0)
	);
	end component;

	-- UUT signals
	signal A_TB: std_logic_vector(3 downto 0);
	signal S_TB: std_logic_vector(1 downto 0);


begin
	UUT: decoder port map(	
		A 			=> A_TB ,
		S			=> S_TB
	);
	
stimulus: process 
begin
	S_TB <= "00";
	wait for 10 ns;
	S_TB <= "01";
	wait for 10 ns;
	S_TB <= "10";
	wait for 10 ns;
	S_TB <= "11";
	wait;
		
		
end process;

end architecture;