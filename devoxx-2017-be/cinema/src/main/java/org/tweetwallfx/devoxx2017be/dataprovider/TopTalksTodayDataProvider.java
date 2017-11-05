/*
 * The MIT License
 *
 * Copyright 2017 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.devoxx2017be.dataprovider;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.VotingResultTalk;
import org.tweetwall.devoxx.api.cfp.client.VotingResults;
import org.tweetwallfx.controls.dataprovider.DataProvider;
import org.tweetwallfx.tweet.api.TweetStream;

/**
 * DataProvider Implementation for Top Talks Today
 * @author Sven Reimers
 */
public class TopTalksTodayDataProvider implements DataProvider {

    private VotingResults votingResults;

    private TopTalksTodayDataProvider() {
        updateVotigResults();
    }

    private void updateVotigResults() {
        String actualDayName = LocalDateTime.now().getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        votingResults = CFPClient.getClient().getVotingResultsDaily(System.getProperty("org.tweetwalfx.devoxxbe17.day", actualDayName));
    }

    public List<VotedTalk> getFilteredSessionData() {
        return votingResults.getResult().getTalks().stream()
                .sorted(Comparator.comparing(VotingResultTalk::getRatingAverageScore).thenComparing(VotingResultTalk::getRatingTotalVotes).reversed())
                .limit(5)
                .map(talk -> 
                    new VotedTalk(talk.getProposalsSpeakers(),
                            talk.getRatingAverageScore(),
                            talk.getRatingTotalVotes(),
                            talk.getProposalId(),
                            talk.getProposalTitle())
                )
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "TRTT-Devoxx2017BE";
    }

    public static class Factory implements DataProvider.Factory {

        @Override
        public DataProvider create(TweetStream tweetStream) {
            return new TopTalksTodayDataProvider();
        }

    }

}