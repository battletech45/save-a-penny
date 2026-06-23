package com.saveapenny.stock.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saveapenny.stock.domain.BalanceSheetResponse;
import com.saveapenny.stock.domain.CashFlowResponse;
import com.saveapenny.stock.domain.CompanyOverview;
import com.saveapenny.stock.domain.DailyTimeSeriesResponse;
import com.saveapenny.stock.domain.EmaResponse;
import com.saveapenny.stock.domain.GlobalQuote;
import com.saveapenny.stock.domain.GlobalQuoteResponse;
import com.saveapenny.stock.domain.IncomeStatementResponse;
import com.saveapenny.stock.domain.NewsArticle;
import com.saveapenny.stock.domain.NewsSentimentResponse;
import com.saveapenny.stock.domain.RsiResponse;
import com.saveapenny.stock.domain.SmaResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AlphaVantageClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesWrappedGlobalQuotePayload() throws Exception {
        String json = """
                {
                  "Global Quote": {
                    "01. symbol": "IBM",
                    "02. open": "175.0000",
                    "03. high": "177.0000",
                    "04. low": "174.5000",
                    "05. price": "176.3000",
                    "06. volume": "5000000",
                    "07. latest trading day": "2025-06-20",
                    "08. previous close": "174.9000",
                    "09. change": "1.4000",
                    "10. change percent": "0.8008%"
                  }
                }
                """;

        GlobalQuoteResponse response = objectMapper.readValue(json, GlobalQuoteResponse.class);
        assertNotNull(response);
        GlobalQuote quote = response.globalQuote();
        assertNotNull(quote);
        assertEquals("IBM", quote.symbol());
        assertEquals("175.0000", quote.open());
        assertEquals("177.0000", quote.high());
        assertEquals("174.5000", quote.low());
        assertEquals("176.3000", quote.price());
        assertEquals("5000000", quote.volume());
        assertEquals("2025-06-20", quote.latestTradingDay());
        assertEquals("174.9000", quote.previousClose());
        assertEquals("1.4000", quote.change());
        assertEquals("0.8008%", quote.changePercent());
    }

    @Test
    void parsesWrappedGlobalQuotePayload_withNullGlobalQuote() throws Exception {
        String json = """
                {
                    "Global Quote": null
                }
                """;

        GlobalQuoteResponse response = objectMapper.readValue(json, GlobalQuoteResponse.class);
        assertNotNull(response);
        assertNull(response.globalQuote());
    }

    @Test
    void throwsOnProviderErrorMessage() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        GlobalQuoteResponse response = objectMapper.readValue(json, GlobalQuoteResponse.class);
        assertNotNull(response);
        assertNull(response.globalQuote());
    }

    @Test
    void parsesGlobalQuoteResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        GlobalQuoteResponse response = objectMapper.readValue(json, GlobalQuoteResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.globalQuote());
    }

    @Test
    void parsesGlobalQuoteResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        GlobalQuoteResponse response = objectMapper.readValue(json, GlobalQuoteResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.globalQuote());
    }

    @Test
    void parsesDailyTimeSeriesPayload() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1. Information": "Daily Prices (open, high, low, close) and Volumes",
                    "2. Symbol": "IBM",
                    "3. Last Refreshed": "2025-06-20",
                    "4. Output Size": "Compact",
                    "5. Time Zone": "US/Eastern"
                  },
                  "Time Series (Daily)": {
                    "2025-06-20": {
                      "1. open": "175.0000",
                      "2. high": "177.0000",
                      "3. low": "174.5000",
                      "4. close": "176.3000",
                      "5. volume": "5000000"
                    },
                    "2025-06-19": {
                      "1. open": "174.5000",
                      "2. high": "176.0000",
                      "3. low": "173.8000",
                      "4. close": "175.2000",
                      "5. volume": "4500000"
                    }
                  }
                }
                """;

        DailyTimeSeriesResponse response = objectMapper.readValue(json, DailyTimeSeriesResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertEquals("IBM", response.metaData().symbol());
        assertEquals("Compact", response.metaData().outputSize());
        assertNotNull(response.timeSeries());
        assertEquals(2, response.timeSeries().size());
        assertTrue(response.timeSeries().containsKey("2025-06-20"));
        assertTrue(response.timeSeries().containsKey("2025-06-19"));

        var point = response.timeSeries().get("2025-06-20");
        assertEquals("175.0000", point.open());
        assertEquals("177.0000", point.high());
        assertEquals("174.5000", point.low());
        assertEquals("176.3000", point.close());
        assertEquals("5000000", point.volume());
    }

    @Test
    void parsesDailyTimeSeriesPayload_withErrorResponse() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        DailyTimeSeriesResponse response = objectMapper.readValue(json, DailyTimeSeriesResponse.class);
        assertNotNull(response);
        assertNull(response.metaData());
        assertNull(response.timeSeries());
    }

    @Test
    void parsesDailyTimeSeriesResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        DailyTimeSeriesResponse response = objectMapper.readValue(json, DailyTimeSeriesResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.metaData());
        assertNull(response.timeSeries());
    }

    @Test
    void parsesDailyTimeSeriesResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        DailyTimeSeriesResponse response = objectMapper.readValue(json, DailyTimeSeriesResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.metaData());
        assertNull(response.timeSeries());
    }

    @Test
    void parsesDailyTimeSeriesPayload_withSparseTimeSeries() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1. Information": "Daily Prices (open, high, low, close) and Volumes",
                    "2. Symbol": "IBM",
                    "3. Last Refreshed": "2025-06-20",
                    "4. Output Size": "Compact",
                    "5. Time Zone": "US/Eastern"
                  },
                  "Time Series (Daily)": {
                    "2025-06-20": {
                      "1. open": "175.0000",
                      "4. close": "176.3000"
                    }
                  }
                }
                """;

        DailyTimeSeriesResponse response = objectMapper.readValue(json, DailyTimeSeriesResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertNotNull(response.timeSeries());
        assertEquals(1, response.timeSeries().size());

        var point = response.timeSeries().get("2025-06-20");
        assertEquals("175.0000", point.open());
        assertEquals("176.3000", point.close());
        assertNull(point.high());
        assertNull(point.low());
        assertNull(point.volume());
    }

    @Test
    void parsesNewsSentimentPayload() throws Exception {
        String json = """
                {
                  "items": "2",
                  "feed": [
                    {
                      "title": "IBM Launches New AI Platform",
                      "url": "https://example.com/ibm-ai",
                      "time_published": "20250120T100000",
                      "summary": "IBM announced a new AI platform today.",
                      "source": "ExampleNews",
                      "overall_sentiment_score": 0.4567,
                      "overall_sentiment_label": "Bullish",
                      "ticker_sentiment": [
                        {
                          "ticker": "IBM",
                          "relevance_score": "0.9",
                          "ticker_sentiment_score": 0.4567,
                          "ticker_sentiment_label": "Bullish"
                        }
                      ]
                    }
                  ]
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals("2", response.items());
        assertNotNull(response.feed());
        assertEquals(1, response.feed().size());

        NewsArticle article = response.feed().get(0);
        assertEquals("IBM Launches New AI Platform", article.title());
        assertEquals("https://example.com/ibm-ai", article.url());
        assertEquals("20250120T100000", article.timePublished());
        assertEquals("ExampleNews", article.source());
        assertEquals(new BigDecimal("0.4567"), article.overallSentimentScore());
        assertEquals("Bullish", article.overallSentimentLabel());

        assertNotNull(article.tickerSentiment());
        assertEquals(1, article.tickerSentiment().size());
        var ts = article.tickerSentiment().get(0);
        assertEquals("IBM", ts.ticker());
        assertEquals("0.9", ts.relevanceScore());
        assertEquals(new BigDecimal("0.4567"), ts.tickerSentimentScore());
        assertEquals("Bullish", ts.tickerSentimentLabel());
    }

    @Test
    void parsesNewsSentimentPayload_withEmptyFeed() throws Exception {
        String json = """
                {
                  "items": "0",
                  "feed": []
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals("0", response.items());
        assertNotNull(response.feed());
        assertTrue(response.feed().isEmpty());
    }

    @Test
    void parsesNewsSentimentResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.items());
        assertNull(response.feed());
    }

    @Test
    void parsesNewsSentimentResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.items());
        assertNull(response.feed());
    }

    @Test
    void parsesNewsSentimentPayload_withSparseArticle() throws Exception {
        String json = """
                {
                  "items": "1",
                  "feed": [
                    {
                      "title": "IBM News",
                      "overall_sentiment_score": 0.1234,
                      "overall_sentiment_label": "Somewhat-Bullish"
                    }
                  ]
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals("1", response.items());
        assertNotNull(response.feed());
        assertEquals(1, response.feed().size());

        NewsArticle article = response.feed().get(0);
        assertEquals("IBM News", article.title());
        assertNull(article.url());
        assertNull(article.timePublished());
        assertNull(article.source());
        assertEquals(new BigDecimal("0.1234"), article.overallSentimentScore());
        assertEquals("Somewhat-Bullish", article.overallSentimentLabel());
        assertNull(article.tickerSentiment());
    }

    @Test
    void parsesNewsSentimentPayload_withMissingFeed() throws Exception {
        String json = """
                {
                  "items": "1"
                }
                """;

        NewsSentimentResponse response = objectMapper.readValue(json, NewsSentimentResponse.class);
        assertNotNull(response);
        assertEquals("1", response.items());
        assertNull(response.feed());
    }

    @Test
    void parsesCompanyOverviewPayload() throws Exception {
        String json = """
                {
                  "Symbol": "IBM",
                  "Name": "International Business Machines",
                  "Description": "IBM is a technology company.",
                  "Exchange": "NYSE",
                  "Currency": "USD",
                  "Country": "USA",
                  "Sector": "Technology",
                  "Industry": "Computer Services",
                  "MarketCapitalization": "200000000000",
                  "PERatio": "25.5",
                  "DividendYield": "0.035",
                  "EPS": "8.50",
                  "Beta": "0.85",
                  "52WeekHigh": "200.00",
                  "52WeekLow": "150.00",
                  "50DayMovingAverage": "180.00",
                  "200DayMovingAverage": "175.00",
                  "SharesOutstanding": "900000000"
                }
                """;

        CompanyOverview overview = objectMapper.readValue(json, CompanyOverview.class);
        assertNotNull(overview);
        assertEquals("IBM", overview.symbol());
        assertEquals("International Business Machines", overview.name());
        assertEquals("NYSE", overview.exchange());
        assertEquals("USD", overview.currency());
        assertEquals("Technology", overview.sector());
        assertEquals("25.5", overview.peRatio());
        assertEquals("0.035", overview.dividendYield());
        assertEquals("8.50", overview.eps());
        assertEquals("200000000000", overview.marketCapitalization());
        assertEquals("0.85", overview.beta());
    }

    @Test
    void parsesCompanyOverview_withNullSymbol() throws Exception {
        String json = """
                {
                  "Error Message": "Invalid API call."
                }
                """;

        CompanyOverview overview = objectMapper.readValue(json, CompanyOverview.class);
        assertNotNull(overview);
        assertNull(overview.symbol());
    }

    @Test
    void parsesCompanyOverview_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        CompanyOverview overview = objectMapper.readValue(json, CompanyOverview.class);
        assertNotNull(overview);
        assertEquals("Invalid API call.", overview.errorMessage());
        assertNull(overview.note());
        assertNull(overview.symbol());
    }

    @Test
    void parsesCompanyOverview_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        CompanyOverview overview = objectMapper.readValue(json, CompanyOverview.class);
        assertNotNull(overview);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                overview.note());
        assertNull(overview.errorMessage());
        assertNull(overview.symbol());
    }

    @Test
    void parsesCompanyOverview_withMinimalFields() throws Exception {
        String json = """
                {
                  "Symbol": "IBM",
                  "Name": "International Business Machines"
                }
                """;

        CompanyOverview overview = objectMapper.readValue(json, CompanyOverview.class);
        assertNotNull(overview);
        assertEquals("IBM", overview.symbol());
        assertEquals("International Business Machines", overview.name());
        assertNull(overview.sector());
        assertNull(overview.peRatio());
        assertNull(overview.eps());
    }

    @Test
    void parsesIncomeStatementPayload() throws Exception {
        String json = """
                {
                  "symbol": "IBM",
                  "annualReports": [
                    {
                      "fiscalDateEnding": "2024-12-31",
                      "reportedCurrency": "USD",
                      "totalRevenue": "60000000000",
                      "netIncome": "8000000000",
                      "grossProfit": "35000000000",
                      "ebitda": "18000000000"
                    }
                  ],
                  "quarterlyReports": []
                }
                """;

        IncomeStatementResponse response = objectMapper.readValue(json, IncomeStatementResponse.class);
        assertNotNull(response);
        assertEquals("IBM", response.symbol());
        assertNotNull(response.annualReports());
        assertEquals(1, response.annualReports().size());

        var report = response.annualReports().get(0);
        assertEquals("2024-12-31", report.fiscalDateEnding());
        assertEquals("USD", report.reportedCurrency());
        assertEquals("60000000000", report.totalRevenue());
        assertEquals("8000000000", report.netIncome());
        assertEquals("35000000000", report.grossProfit());
        assertEquals("18000000000", report.ebitda());

        assertNotNull(response.quarterlyReports());
        assertTrue(response.quarterlyReports().isEmpty());
    }

    @Test
    void parsesIncomeStatementResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        IncomeStatementResponse response = objectMapper.readValue(json, IncomeStatementResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesIncomeStatementResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        IncomeStatementResponse response = objectMapper.readValue(json, IncomeStatementResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesIncomeStatementResponse_withEmptyReports() throws Exception {
        String json = """
                {
                  "symbol": "IBM",
                  "annualReports": [],
                  "quarterlyReports": []
                }
                """;

        IncomeStatementResponse response = objectMapper.readValue(json, IncomeStatementResponse.class);
        assertNotNull(response);
        assertEquals("IBM", response.symbol());
        assertNotNull(response.annualReports());
        assertTrue(response.annualReports().isEmpty());
        assertNotNull(response.quarterlyReports());
        assertTrue(response.quarterlyReports().isEmpty());
    }

    @Test
    void parsesBalanceSheetPayload() throws Exception {
        String json = """
                {
                  "symbol": "IBM",
                  "annualReports": [
                    {
                      "fiscalDateEnding": "2024-12-31",
                      "reportedCurrency": "USD",
                      "totalAssets": "150000000000",
                      "totalLiabilities": "100000000000",
                      "totalShareholderEquity": "50000000000"
                    }
                  ],
                  "quarterlyReports": []
                }
                """;

        BalanceSheetResponse response = objectMapper.readValue(json, BalanceSheetResponse.class);
        assertNotNull(response);
        assertEquals("IBM", response.symbol());
        assertNotNull(response.annualReports());
        assertEquals(1, response.annualReports().size());

        var report = response.annualReports().get(0);
        assertEquals("2024-12-31", report.fiscalDateEnding());
        assertEquals("USD", report.reportedCurrency());
        assertEquals("150000000000", report.totalAssets());
        assertEquals("100000000000", report.totalLiabilities());
        assertEquals("50000000000", report.totalShareholderEquity());
    }

    @Test
    void parsesBalanceSheetResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        BalanceSheetResponse response = objectMapper.readValue(json, BalanceSheetResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesBalanceSheetResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        BalanceSheetResponse response = objectMapper.readValue(json, BalanceSheetResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesBalanceSheetPayload_withSparseItem() throws Exception {
        String json = """
                {
                  "symbol": "IBM",
                  "annualReports": [
                    {
                      "fiscalDateEnding": "2024-12-31",
                      "totalAssets": "150000000000"
                    }
                  ],
                  "quarterlyReports": []
                }
                """;

        BalanceSheetResponse response = objectMapper.readValue(json, BalanceSheetResponse.class);
        assertNotNull(response);
        assertEquals("IBM", response.symbol());
        assertNotNull(response.annualReports());
        assertEquals(1, response.annualReports().size());

        var report = response.annualReports().get(0);
        assertEquals("2024-12-31", report.fiscalDateEnding());
        assertEquals("150000000000", report.totalAssets());
        assertNull(report.reportedCurrency());
        assertNull(report.totalLiabilities());
        assertNull(report.totalShareholderEquity());
    }

    @Test
    void parsesCashFlowPayload() throws Exception {
        String json = """
                {
                  "symbol": "IBM",
                  "annualReports": [
                    {
                      "fiscalDateEnding": "2024-12-31",
                      "reportedCurrency": "USD",
                      "operatingCashflow": "15000000000",
                      "capitalExpenditures": "5000000000",
                      "netIncome": "8000000000"
                    }
                  ],
                  "quarterlyReports": []
                }
                """;

        CashFlowResponse response = objectMapper.readValue(json, CashFlowResponse.class);
        assertNotNull(response);
        assertEquals("IBM", response.symbol());
        assertNotNull(response.annualReports());
        assertEquals(1, response.annualReports().size());

        var report = response.annualReports().get(0);
        assertEquals("2024-12-31", report.fiscalDateEnding());
        assertEquals("USD", report.reportedCurrency());
        assertEquals("15000000000", report.operatingCashflow());
        assertEquals("5000000000", report.capitalExpenditures());
        assertEquals("8000000000", report.netIncome());
    }

    @Test
    void parsesCashFlowResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        CashFlowResponse response = objectMapper.readValue(json, CashFlowResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesCashFlowResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        CashFlowResponse response = objectMapper.readValue(json, CashFlowResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.symbol());
        assertNull(response.annualReports());
        assertNull(response.quarterlyReports());
    }

    @Test
    void parsesSmaPayload() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1: Symbol": "IBM",
                    "2: Indicator": "SMA",
                    "3: Last Refreshed": "2025-06-20",
                    "4: Interval": "daily",
                    "5: Time Period": "20",
                    "6: Series Type": "close",
                    "7: Time Zone": "US/Eastern"
                  },
                  "Technical Analysis: SMA": {
                    "2025-06-20": { "SMA": "178.4567" },
                    "2025-06-19": { "SMA": "177.8901" }
                  }
                }
                """;

        SmaResponse response = objectMapper.readValue(json, SmaResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertEquals("IBM", response.metaData().symbol());
        assertEquals("daily", response.metaData().interval());
        assertEquals("20", response.metaData().timePeriod());

        assertNotNull(response.dataPoints());
        assertEquals(2, response.dataPoints().size());
        assertEquals("178.4567", response.dataPoints().get("2025-06-20").value());
        assertEquals("177.8901", response.dataPoints().get("2025-06-19").value());
    }

    @Test
    void parsesTechnicalIndicator_withErrorResponse() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        SmaResponse response = objectMapper.readValue(json, SmaResponse.class);
        assertNotNull(response);
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }

    @Test
    void parsesSmaResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        SmaResponse response = objectMapper.readValue(json, SmaResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }

    @Test
    void parsesSmaPayload_withEmptyDataPoints() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1: Symbol": "IBM",
                    "2: Indicator": "SMA",
                    "3: Last Refreshed": "2025-06-20",
                    "4: Interval": "daily",
                    "5: Time Period": "20",
                    "6: Series Type": "close",
                    "7: Time Zone": "US/Eastern"
                  },
                  "Technical Analysis: SMA": {}
                }
                """;

        SmaResponse response = objectMapper.readValue(json, SmaResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertEquals("IBM", response.metaData().symbol());
        assertNotNull(response.dataPoints());
        assertTrue(response.dataPoints().isEmpty());
    }

    @Test
    void parsesEmaPayload() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1: Symbol": "IBM",
                    "2: Indicator": "EMA",
                    "3: Last Refreshed": "2025-06-20",
                    "4: Interval": "daily",
                    "5: Time Period": "20",
                    "6: Series Type": "close",
                    "7: Time Zone": "US/Eastern"
                  },
                  "Technical Analysis: EMA": {
                    "2025-06-20": { "EMA": "179.1234" },
                    "2025-06-19": { "EMA": "178.5678" }
                  }
                }
                """;

        EmaResponse response = objectMapper.readValue(json, EmaResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertEquals("IBM", response.metaData().symbol());

        assertEquals(2, response.dataPoints().size());
        assertEquals("179.1234", response.dataPoints().get("2025-06-20").value());
    }

    @Test
    void parsesEmaResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        EmaResponse response = objectMapper.readValue(json, EmaResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }

    @Test
    void parsesEmaResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        EmaResponse response = objectMapper.readValue(json, EmaResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }

    @Test
    void parsesRsiPayload() throws Exception {
        String json = """
                {
                  "Meta Data": {
                    "1: Symbol": "IBM",
                    "2: Indicator": "RSI",
                    "3: Last Refreshed": "2025-06-20",
                    "4: Interval": "daily",
                    "5: Time Period": "14",
                    "6: Series Type": "close",
                    "7: Time Zone": "US/Eastern"
                  },
                  "Technical Analysis: RSI": {
                    "2025-06-20": { "RSI": "55.4321" },
                    "2025-06-19": { "RSI": "54.9876" }
                  }
                }
                """;

        RsiResponse response = objectMapper.readValue(json, RsiResponse.class);
        assertNotNull(response);
        assertNotNull(response.metaData());
        assertEquals("IBM", response.metaData().symbol());

        assertEquals(2, response.dataPoints().size());
        assertEquals("55.4321", response.dataPoints().get("2025-06-20").value());
    }

    @Test
    void parsesRsiResponse_withProviderError() throws Exception {
        String json = """
                {
                    "Error Message": "Invalid API call."
                }
                """;

        RsiResponse response = objectMapper.readValue(json, RsiResponse.class);
        assertNotNull(response);
        assertEquals("Invalid API call.", response.errorMessage());
        assertNull(response.note());
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }

    @Test
    void parsesRsiResponse_withProviderNote() throws Exception {
        String json = """
                {
                    "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
                }
                """;

        RsiResponse response = objectMapper.readValue(json, RsiResponse.class);
        assertNotNull(response);
        assertEquals(
                "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.",
                response.note());
        assertNull(response.errorMessage());
        assertNull(response.metaData());
        assertNull(response.dataPoints());
    }
}
