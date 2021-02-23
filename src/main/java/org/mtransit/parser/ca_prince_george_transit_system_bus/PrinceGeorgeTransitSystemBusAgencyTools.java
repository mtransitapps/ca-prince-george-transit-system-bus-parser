package org.mtransit.parser.ca_prince_george_transit_system_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Pattern;

import static org.mtransit.commons.Constants.SPACE_;

// https://www.bctransit.com/open-data
// https://www.bctransit.com/data/gtfs/prince-george.zip
public class PrinceGeorgeTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new PrinceGeorgeTransitSystemBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Prince George TS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // use route short name as route ID
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	// private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			int rsn = Integer.parseInt(gRoute.getRouteShortName());
			switch (rsn) {
			// @formatter:off
			case 1: return "004B8D";
			case 5: return "F8931E";
			case 10: return "8CC640";
			case 11: return "8CC63F";
			case 12: return "49176D";
			case 15: return "EC1D8D";
			case 16: return "00B9BF";
			case 17: return "B3AA7E";
			case 18: return "B3AA7E";
			case 46: return "8D0B3A";
			case 47: return "00AA4F";
			case 55: return "00AEEF";
			case 88: return "FFC10E";
			case 89: return "0073AE";
			case 91: return "BF83B9";
			case 96: return "B5BB19";
			case 97: return "367D0F";
			// @formatter:on
			default:
				throw new MTLog.Fatal("Unexpected route color for %s!", gRoute);
			}
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern RTE_1_HERITAGE_ = Pattern.compile("(^heritage (- )?(.*))", Pattern.CASE_INSENSITIVE); // fix route 1
	private static final String RTE_1_HERITAGE_REPLACEMENT = "$3";

	@NotNull
	@Override
	public String cleanDirectionHeadsign(boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = RTE_1_HERITAGE_.matcher(directionHeadSign).replaceAll(RTE_1_HERITAGE_REPLACEMENT); // FIX route #1
		directionHeadSign = super.cleanDirectionHeadsign(fromStopName, directionHeadSign);
		return directionHeadSign;
	}

	private static final Pattern _DASH_ = Pattern.compile("( - )");

	private static final Pattern UNBC_ = CleanUtils.cleanWords("unbc");
	private static final String UNBC_REPLACEMENT = CleanUtils.cleanWordsReplacement("UNBC");

	private static final Pattern KRSS_ = CleanUtils.cleanWords("krss");
	private static final String KRSS_REPLACEMENT = CleanUtils.cleanWordsReplacement("KRSS");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(SPACE_);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = KRSS_.matcher(tripHeadsign).replaceAll(KRSS_REPLACEMENT);
		tripHeadsign = UNBC_.matcher(tripHeadsign).replaceAll(UNBC_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = KRSS_.matcher(gStopName).replaceAll(KRSS_REPLACEMENT);
		gStopName = UNBC_.matcher(gStopName).replaceAll(UNBC_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
