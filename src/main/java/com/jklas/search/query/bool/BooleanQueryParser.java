package com.jklas.search.query.bool;

import java.util.List;
import java.util.Stack;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.processor.QueryTextProcessor;
import com.jklas.search.engine.processor.NormalizeTokenizeProcessor;
import com.jklas.search.index.Term;
import com.jklas.search.query.operator.AndOperator;
import com.jklas.search.query.operator.Operator;
import com.jklas.search.query.operator.OrOperator;
import com.jklas.search.query.operator.RetrieveOperator;

public class BooleanQueryParser {

	private static BooleanPostingListExtractor extractor = new BooleanPostingListExtractor();
	
	private final String plainTextQuery;
	
	private final Language queryLanguage;

	private final QueryTextProcessor textProcessor;
	
	public BooleanQueryParser(String originalQuery) {
		this(originalQuery, Language.UNKOWN_LANGUAGE); 
	}
	
	public BooleanQueryParser(String originalQuery, Language language) {
		this(originalQuery, language, new NormalizeTokenizeProcessor() ); 
	}

	public BooleanQueryParser(String originalQuery, QueryTextProcessor queryTextProcessor) {
		this(originalQuery, Language.UNKOWN_LANGUAGE, queryTextProcessor);
	}
	
	public BooleanQueryParser(String originalQuery, Language language, QueryTextProcessor queryTextProcessor) {
		
		if(originalQuery == null) throw new IllegalArgumentException("Can't parse a null query");
		if(language == null) throw new IllegalArgumentException("Can't work with a null language... (UNKNOWN_LANGUAGE is allowed, but not null)");
		if(queryTextProcessor == null) throw new IllegalArgumentException("Can't work with a null text processor");
		
		this.plainTextQuery = originalQuery;
		this.queryLanguage = language;
		this.textProcessor = queryTextProcessor;
	}

	public BooleanQuery getQuery() {
		List<Term> queryTokens = textProcessor.processText(plainTextQuery, queryLanguage);

		removeLeadingAnd(queryTokens);

		int tokenCount = queryTokens.size();

		if(tokenCount == 0)
			throw new IllegalArgumentException("Query must contain at least one term");		
		else
			if(tokenCount == 1) 
				return buildOneTokenQuery(queryTokens);
			else if(tokenCount == 2)
				return buildTwoTokenQuery(queryTokens);
			else {	
				return new BooleanQuery( buildOperatorForMultiTokenQuery(queryTokens, tokenCount) );
			}		
	}

	private Operator<ObjectKeyResult> buildOperatorForMultiTokenQuery(List<Term> queryTokens, int tokenCount) {
		Operator<ObjectKeyResult> root;
		int lastOrAt = -1;
		Stack<Operator<ObjectKeyResult>> operatorStack = new Stack<Operator<ObjectKeyResult>>();

		for (int i = 0; i < tokenCount; i++) {
			Term currentTerm = queryTokens.get(i);
			boolean isLastTerm = (i == tokenCount -1);

			if(OrOperator.isOperator(currentTerm)) {
				if(isLastTerm) throw new IllegalArgumentException("Bad query syntax, last token is a binary operator");
				operatorStack.push(recursiveLeftToRightAnd(queryTokens, lastOrAt+1, i-1));
				lastOrAt = i;
			} else {
				if(isLastTerm) {
					operatorStack.push(recursiveLeftToRightAnd(queryTokens, lastOrAt+1, tokenCount-1));
				}
			}
		}

		if(operatorStack.size()==1) {
			root = operatorStack.pop();
		} else {
			root = buildOperatorsFromStack(operatorStack.pop(),operatorStack);			
		}
		return root;
	}

	private BooleanQuery buildTwoTokenQuery(List<Term> queryTokens) {
		Term firstToken  = queryTokens.get(0);
		Term secondToken = queryTokens.get(1);

		if(OrOperator.isOperator(firstToken) || OrOperator.isOperator(secondToken) || AndOperator.isOperator(firstToken)
				|| AndOperator.isOperator(secondToken))
			throw new IllegalArgumentException("Bad query syntax, can't use binary operator without two terms");
		
		return new BooleanQuery(new AndOperator<ObjectKeyResult>(new RetrieveOperator<ObjectKeyResult>(firstToken, extractor),
				new RetrieveOperator<ObjectKeyResult>(secondToken,extractor)));
	}

	private BooleanQuery buildOneTokenQuery(List<Term> queryTokens) {
		Term firstToken = queryTokens.get(0);

		if(AndOperator.isOperator(firstToken) || OrOperator.isOperator(firstToken))
			throw new IllegalArgumentException("Bad query syntax, can't use binary operator without two terms");

		return new BooleanQuery(new RetrieveOperator<ObjectKeyResult>(firstToken,extractor));
	}

	private void removeLeadingAnd(List<Term> queryTokens) {
		if(queryTokens.size()>0 && AndOperator.isOperator(queryTokens.get(0))) queryTokens.remove(0);
	}

	public Operator<ObjectKeyResult> buildOperatorsFromStack(Operator<ObjectKeyResult> left, Stack<Operator<ObjectKeyResult>> operatorStack)
	{
		if(operatorStack.size()>1)
			return new OrOperator<ObjectKeyResult>(left, buildOperatorsFromStack(operatorStack.pop(), operatorStack));
		else
			return new OrOperator<ObjectKeyResult>(operatorStack.pop(),left);
	}

	private Operator<ObjectKeyResult> recursiveLeftToRightAnd(List<Term> queryTokens, int current, int rightmost) {
		Term currentTerm = queryTokens.get(current);
		if(current == rightmost) {
			return new RetrieveOperator<ObjectKeyResult>(currentTerm,extractor);
		} else {
			if(AndOperator.isOperator(currentTerm)) 
				return recursiveLeftToRightAnd(queryTokens, current+1, rightmost);
			else
				return new AndOperator<ObjectKeyResult>( new RetrieveOperator<ObjectKeyResult>(currentTerm ,extractor),
						recursiveLeftToRightAnd(queryTokens, current+1, rightmost) );
		}
	}

}
