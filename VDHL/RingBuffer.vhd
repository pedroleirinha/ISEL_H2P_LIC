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
			full, empty:										OUT std_logic
		);
	END component;
	
	
	component RingBufferControl
		PORT(
			clk_in, DAV, CTS, full, empty: 			IN std_logic;
			Wreg, DAC, Wr, selPG, incPut, incGet:	OUT std_logic
		);
	END component;

	component RandomAccessMemory
		PORT(
			clk_in, Wr:		in  std_logic;                         
			Addr, dataIn: 	in  std_logic_vector(3 downto 0);      
			dataOut:			out std_logic_vector(3 downto 0)  
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
		incGet   =>		incGet
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
	
	ram: RandomAccessMemory port map(
		clk_in	=>		clk_in,
		Wr			=>		writeRegister,
		Addr		=>		ramAddr,
		dataIn	=>		D,
		dataOut	=>		Q
	);
	
	

END Behaviour;