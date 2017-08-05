package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedFile;

public interface ITokenizer {
	public TokenizedFile run(String originalFilePath, TokenizedFile file);
}
