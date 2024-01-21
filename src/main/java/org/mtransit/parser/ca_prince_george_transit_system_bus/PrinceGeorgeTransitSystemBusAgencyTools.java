package org.mtransit.parser.ca_prince_george_transit_system_bus;

import static org.mtransit.commons.Constants.SPACE_;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.Cleaner;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;

// https://www.bctransit.com/open-data
public class PrinceGeorgeTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new PrinceGeorgeTransitSystemBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
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
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false; // route ID used by GTFS RT
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "\\-[A-Z]+$";
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
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
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final int rsn = Integer.parseInt(gRoute.getRouteShortName());
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
		case 19: return "B978B3";
		case 46: return "8D0B3A";
		case 47: return "00AA4F";
		case 55: return "00AEEF";
		case 88: return "FFC10E";
		case 89: return "0073AE";
		case 91: return "BF83B9";
		case 96: return "B5BB19";
		case 97: return "367D0F";
		case 105: return "776EAF";
		case 161: return "0F6534";
		case 162: return null; // TODO
		// @formatter:on
		default:
			throw new MTLog.Fatal("Unexpected route color for %s!", gRoute.toStringPlus());
		}
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Cleaner RTE_1_HERITAGE_ = new Cleaner(
			"(^heritage (- )?(.*))",
			"$3",
			true
	); // fix route 1

	@NotNull
	@Override
	public String cleanDirectionHeadsign(int directionId, boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = RTE_1_HERITAGE_.clean(directionHeadSign); // FIX route #1
		directionHeadSign = super.cleanDirectionHeadsign(directionId, fromStopName, directionHeadSign);
		return directionHeadSign;
	}

	private static final Cleaner _DASH_ = new Cleaner(
			"( - )",
			SPACE_
	);

	private static final Cleaner UNBC_ = new Cleaner(
			Cleaner.matchWords("unbc"),
			"UNBC",
			true
	);

	private static final Cleaner KRSS_ = new Cleaner(
			Cleaner.matchWords("krss"),
			"KRSS",
			true
	);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = _DASH_.clean(tripHeadsign);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = KRSS_.clean(tripHeadsign);
		tripHeadsign = UNBC_.clean(tripHeadsign);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = KRSS_.clean(gStopName);
		gStopName = UNBC_.clean(gStopName);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
