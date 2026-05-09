library ieee;
use ieee.std_logic_1164.all;

ENTITY RingBuffer IS
	PORT(
		clk_in, DAV, CTS, CLEAR: 	IN std_logic;
		D: 								IN std_logic_vector(3 downto 0);
		Q: 								OUT std_logic_vector (3 downto 0);
		Wreg, DAC:						OUT std_logic
	);
END RingBuffer;

ARCHITECTURE Behaviour OF RingBuffer IS

	component MemoryAddressControl
		PORT(
			clk_in, putGet, CLEAR, incPut, incGet: IN std_logic;
			Q:		 											OUT std_logic_vector(3 downto 0);
			full, empty:									OUT std_logic
		);
	END component;
	
	
	component RingBufferControl
		PORT(
			clk_in, DAV, CTS, full, empty, CLEAR:	IN std_logic;
			Wreg, DAC, Wr, selPG, incPut, incGet:	OUT std_logic
		);
	END component;

	component RAM
		PORT(
			wr:		in  std_logic;                         
			address, din: 	in  std_logic_vector(3 downto 0);      
			dout:			out std_logic_vector(3 downto 0)  
		);
	END component;
	
	signal fullSignal, emptySignal, writeRegister, selPG, incPut, incGet: std_logic;
	signal ramAddr: std_logic_vector(3 downto 0);
		
BEGIN

	rbc: RingBufferControl port map(
		clk_in	=>		clk_in,
		DAV		=>		DAV,
		CTS		=>		CTS,
		full		=>		fullSignal,
		empty		=>		emptySignal,
		Wreg		=>		Wreg,
		DAC		=>		DAC,
		Wr			=>		writeRegister,
		selPG		=>		selPG,
		incPut	=> 	incPut,
		incGet   =>		incGet,
		CLEAR		=>		CLEAR
	);
	
	mac: MemoryAddressControl port map(
		clk_in	=>		clk_in,
		putGet	=>		selPG,
		CLEAR		=>		CLEAR,
		incPut	=>		incPut,
		incGet	=>		incGet,
		Q			=>		ramAddr,
		full		=>		fullSignal,
		empty		=>		emptySignal
	
	);
	
	ram1: RAM port map(
		wr			=>		writeRegister,
		address	=>		ramAddr,
		din		=>		D,
		dout		=>		Q
	);
	
	
	
	

END Behaviour;